package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.initialize.InitializeVirusEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.logic.operator.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.operator.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.operator.initializeVirusPopulation.BasicInitializationOfVirusPopulation
import hu.raven.puppet.logic.operator.initializeVirusPopulation.InitializeVirusPopulation
import hu.raven.puppet.logic.step.booststrategy.BoostOnBestLazy
import hu.raven.puppet.logic.step.booststrategy.BoostStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.CrossOverStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.HalfElitistCrossover
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.mutatechildren.MutateChildrenByReverse
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivorsMultyObjectiveHalfElitist
import hu.raven.puppet.logic.step.transcription.Transcription
import hu.raven.puppet.logic.step.transcription.VegaTranscription
import hu.raven.puppet.logic.step.transduction.Transduction
import hu.raven.puppet.logic.step.transduction.VegaTransduction
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.KoinUtil
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path

//iterationOfCreation=1550, cost=[17191.0], 64 virus, 8 length, 0.25, 0.75, 0.75
//iterationOfCreation=1713, cost=[17303.0], 64 virus, 8 length, 0.1, 0.5, 0.5
//iterationOfCreation=707, cost=[17385.0], 16 virus, 8 length, 0.01, 0.5, 0.5
//iterationOfCreation=758, cost=[17369.0], 64 virus, 8 length, 1 / 64, 0.5, 0.5
//iterationOfCreation=1066, cost=[17305.0], 64 virus, 16 length, 1 / 128, 0.5, 0.5
//NEW TSP DATASET
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
                SelectSurvivorsMultyObjectiveHalfElitist
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