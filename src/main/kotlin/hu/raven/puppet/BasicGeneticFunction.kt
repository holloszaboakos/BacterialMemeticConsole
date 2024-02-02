package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames.*
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
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
import hu.raven.puppet.logic.step.boost_strategy.BoostOnBestLazy
import hu.raven.puppet.logic.step.boost_strategy.BoostStrategy
import hu.raven.puppet.logic.step.crossover_strategy.CrossOverStrategy
import hu.raven.puppet.logic.step.crossover_strategy.HalfElitistCrossover
import hu.raven.puppet.logic.step.mutate_children.MutateChildren
import hu.raven.puppet.logic.step.mutate_children.MutateChildrenByReverse
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivors
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivorsMultiObjectiveHalfElitist
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.KoinUtil.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path

//64-0 17312
//64-0 17385
//64-0 17487
//64-0 iterationOfCreation=860, cost=[17305.0]
//64-0 iterationOfCreation=663, cost=[17305.0]
fun main() {
    startKoin {
        modules(module {
            single(named(SINGLE_FILE)) { "size64instance0.json" }
            single(named(INPUT_FOLDER)) { "\\input\\tsp" }
            single(named(OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
            single<InitializeAlgorithm<*>> {
                InitializeEvolutionaryAlgorithm(
                    initializePopulation = get(),
                    orderPopulationByCost = get()
                )
            }
            single<InitializePopulation> {
                InitializePopulationByModuloStepper(64 * 64)
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
                    fileName = get(named(SINGLE_FILE))
                )
            }
            single<ObjectLoggerService<*>> {
                ObjectLoggerService<String>(outputPath = get(named(OUTPUT_FOLDER)))
            }
            single {
                get<TaskLoaderService>().loadTask(folderPath = get(named(INPUT_FOLDER)))
            }
            single<AlgorithmIteration<*>> {
                EvolutionaryAlgorithmIteration(
                    steps = arrayOf(
                        //get<BoostStrategy>(),
                        get<SelectSurvivors>(),
                        get<CrossOverStrategy>(),
                        get<MutateChildren>(),
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
        })
    }

    val initialization: InitializeAlgorithm<EvolutionaryAlgorithmState> = get()
    val iteration: AlgorithmIteration<EvolutionaryAlgorithmState> = get()
    val state = initialization(get())
    repeat(10_000) {
        iteration(state)
        print("iteration $it.: ")
        println(state.copyOfBest)
    }

    stopKoin()
}