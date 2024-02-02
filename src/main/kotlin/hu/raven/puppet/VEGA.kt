package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.initialize.InitializeVirusEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.logic.operator.boost_operator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.logic.operator.crossover_operator.HeuristicCrossOver
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.operator.initialize_virus_population.BasicInitializationOfVirusPopulation
import hu.raven.puppet.logic.operator.initialize_virus_population.InitializeVirusPopulation
import hu.raven.puppet.logic.step.boost_strategy.BoostOnBestLazy
import hu.raven.puppet.logic.step.boost_strategy.BoostStrategy
import hu.raven.puppet.logic.step.crossover_strategy.CrossOverStrategy
import hu.raven.puppet.logic.step.crossover_strategy.HalfElitistCrossover
import hu.raven.puppet.logic.step.mutate_children.MutateChildren
import hu.raven.puppet.logic.step.mutate_children.MutateChildrenByReverse
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivors
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivorsMultiObjectiveHalfElitist
import hu.raven.puppet.logic.step.virus_transcription.Transcription
import hu.raven.puppet.logic.step.virus_transcription.VegaTranscription
import hu.raven.puppet.logic.step.virus_transduction.Transduction
import hu.raven.puppet.logic.step.virus_transduction.VegaTransduction
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.KoinUtil
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path


//iterationOfCreation=3308, cost=[16219.0], 64 virus, 16 length, 1 / 128, 0.5, 0.5
//iterationOfCreation=2181, cost=[16126.0], 64 virus, 16 length, 1 / 128, 0.5, 0.5
//iterationOfCreation=2599, cost=[16241.0], 64 virus, 16 length, 1 / 128, 0.5, 0.5
fun main() {
    startKoin {
        modules(module {
            single(named(FilePathVariableNames.SINGLE_FILE)) { "size64instance0.json" }
            single(named(FilePathVariableNames.INPUT_FOLDER)) { "\\input\\tsp" }
            single(named(FilePathVariableNames.OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
            single<InitializeAlgorithm<*>> {
                InitializeVirusEvolutionaryAlgorithm(
                    InitializeEvolutionaryAlgorithm(
                        initializePopulation = get(),
                        orderPopulationByCost = get()
                    ),
                    get()
                )
            }
            single<InitializePopulation> {
                InitializePopulationByModuloStepper(64 * 64)
            }
            single<InitializeVirusPopulation> {
                BasicInitializationOfVirusPopulation(
                    64,
                    8
                )
            }
            single {
                OrderPopulationByCost(calculateCostOf = get())
            }
            single<CalculateCost> {
                CalculateCostOfTspSolution(task = get())
            }
            single<TaskLoaderService> {
                TspTaskLoaderService(
                    logger = get(),
                    fileName = get(named(FilePathVariableNames.SINGLE_FILE))
                )
            }
            single<ObjectLoggerService<*>> {
                ObjectLoggerService<String>(outputPath = get(named(FilePathVariableNames.OUTPUT_FOLDER)))
            }
            single {
                get<TaskLoaderService>().loadTask(folderPath = get(named(FilePathVariableNames.INPUT_FOLDER)))
            }
            single<AlgorithmIteration<*>> {
                EvolutionaryAlgorithmIteration(
                    steps = arrayOf(
                        //get<BoostStrategy>(),
                        get<SelectSurvivors>(),
                        get<CrossOverStrategy>(),
                        get<MutateChildren>(),
                        get<OrderPopulationByCost>(),
                        get<Transcription>(),
                        get<Transduction>(),
                        get<OrderPopulationByCost>(),
                    )
                )
            }
            single<BoostStrategy> {
                BoostOnBestLazy(
                    boostOperator = get()
                )
            }
            single<BoostOperator<*>> {
                Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder(
                    calculateCostOf = get(),
                    63,
                    64 * 64
                )
            }
            single<SelectSurvivors> {
                SelectSurvivorsMultiObjectiveHalfElitist
            }
            single<CrossOverStrategy> {
                HalfElitistCrossover(
                    crossoverOperators = listOf(get())
                )
            }
            single<CrossOverOperator> {
                HeuristicCrossOver(get<Task>().costGraph)
            }
            single<MutateChildren> {
                MutateChildrenByReverse
            }
            single<Transcription> {
                VegaTranscription(
                    virusInfectionRate = 1 / 64f,
                    lifeReductionRate = 0.5f,
                    lifeCoefficient = 0.5f,
                    get()
                )
            }
            single<Transduction> {
                VegaTransduction
            }
        })
    }

    val initialization: InitializeAlgorithm<VirusEvolutionaryAlgorithmState> = KoinUtil.get()
    val iteration: AlgorithmIteration<VirusEvolutionaryAlgorithmState> = KoinUtil.get()
    val state = initialization(KoinUtil.get())
    repeat(10_000) {
        iteration(state)
        print("iteration $it.: ")
        println(state.copyOfBest)
    }

    stopKoin()
}