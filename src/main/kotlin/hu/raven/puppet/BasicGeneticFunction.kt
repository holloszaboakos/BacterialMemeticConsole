package hu.raven.puppet

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.boostoperator.Opt2Cycle
import hu.raven.puppet.logic.step.booststrategy.BoostOnBestLazy
import hu.raven.puppet.logic.step.booststrategy.BoostStrategy
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.crossoveroperator.OrderCrossOver
import hu.raven.puppet.logic.step.crossoverstrategy.CrossOverStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.HalfElitistCrossover
import hu.raven.puppet.logic.step.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.step.iteration.AlgorithmIteration
import hu.raven.puppet.logic.step.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.mutatechildren.MutateChildrenByReverse
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.modules.FilePathVariableNames.*
import hu.raven.puppet.utility.KoinUtil.get
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path

fun main() {
    startKoin {
        modules(module {
            single(named(SINGLE_FILE)) { "size4instance0.json" }
            single(named(INPUT_FOLDER)) { "\\input\\tsp" }
            single(named(OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
            single<InitializeAlgorithm<*>> {
                InitializeEvolutionaryAlgorithm<Meter>(
                    initializePopulation = get(),
                    orderPopulationByCost = get()
                )
            }
            single<InitializePopulation<*>> {
                InitializePopulationByModuloStepper<Meter>()
            }
            single {
                OrderPopulationByCost<Meter>(calculateCostOf = get())
            }
            single<CalculateCost<*>> {
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
                        get<BoostStrategy<Meter>>(),
                        get<SelectSurvivors<Meter>>(),
                        get<CrossOverStrategy<Meter>>(),
                        get<MutateChildren<Meter>>(),
                        get<OrderPopulationByCost<Meter>>(),
                    )
                )
            }
            single<BoostStrategy<*>> {
                BoostOnBestLazy<Meter>(
                    boostOperator = get()
                )
            }
            single<BoostOperator<*, *>> {
                Opt2Cycle<Meter, OnePartRepresentationWithCost<Meter>>(
                    calculateCostOf = get()
                )
            }
            single<SelectSurvivors<Meter>> {
                SelectSurvivors()
            }
            single<CrossOverStrategy<*>> {
                HalfElitistCrossover<Meter>(
                    crossoverOperators = listOf(get())
                )
            }
            single<CrossOverOperator> {
                OrderCrossOver()
            }
            single<MutateChildren<*>> {
                MutateChildrenByReverse<Meter>()
            }
        })
        val initialization: InitializeAlgorithm<EvolutionaryAlgorithmState<Meter>> = get()
        val iteration: AlgorithmIteration<EvolutionaryAlgorithmState<Meter>> = get()
        val state = initialization(get())
        repeat(100) {
            iteration(state)
            println(state.copyOfBest)
        }
    }
}