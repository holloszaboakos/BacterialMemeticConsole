package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.operator.crossoveroperator.EdgeSelectorCrossOver
import hu.raven.puppet.logic.operator.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.operator.crowdingdistance.BasicCrowdingDistance
import hu.raven.puppet.logic.operator.crowdingdistance.CrowdingDistance
import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.step.crossoverstrategy.CrossOverStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.TournamentCrossoverWithCrowdingDistance
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.mutatechildren.MutateChildrenBySwap
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivorsMultiObjectiveElitistWithCrowdingDistance
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.KoinUtil
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path

//size64instance0
//17201, HeuristicCrossOver
//17252, HeuristicCrossOver
//20521, OrderCrossOver
//18269, OrderBasedCrossover
//16774, EdgeSelectorCrossOver
//16836, EdgeSelectorCrossOver

//size128instance0
//iterationOfCreation=3801, cost=[16291.0], EdgeSelectorCrossOver
//iterationOfCreation=4269, cost=[16741.0], EdgeSelectorCrossOver
//iterationOfCreation=1674, cost=[17964.0], HeuristicCrossOver
//iterationOfCreation=2053, cost=[17837.0], HeuristicCrossOver

//size256instance0
//iterationOfCreation=3801, cost=[16291.0], EdgeSelectorCrossOver
//iterationOfCreation=4269, cost=[16741.0], EdgeSelectorCrossOver
//iterationOfCreation=1674, cost=[17964.0], HeuristicCrossOver
//iterationOfCreation=2053, cost=[17837.0], HeuristicCrossOver

//size562instance0
//iterationOfCreation=5517, cost=[18007.0], HeuristicCrossOver
//iterationOfCreation=9957, cost=[30335.0], EdgeSelectorCrossOver
//iterationOfCreation=23061, cost=[16978.0], EdgeSelectorCrossOver

fun main() {
    val size = 256
    startKoin {
        modules(
            module {
                single(named(FilePathVariableNames.SINGLE_FILE)) { "size${size}instance0.json" }
                single(named(FilePathVariableNames.INPUT_FOLDER)) { "\\input\\tsp" }
                single(named(FilePathVariableNames.OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
                single<AlgorithmIteration<EvolutionaryAlgorithmState>> {
                    EvolutionaryAlgorithmIteration(
                        arrayOf(
                            get<SelectSurvivors>(),
                            get<CrossOverStrategy>(),
                            get<MutateChildren>(),
                            get<OrderPopulationByCost>(),
                        )
                    )
                }
                single<SelectSurvivors> {
                    SelectSurvivorsMultiObjectiveElitistWithCrowdingDistance(
                        get()
                    )
                }
                single<CrowdingDistance> {
                    BasicCrowdingDistance
                }
                single<CrossOverStrategy> {
                    TournamentCrossoverWithCrowdingDistance(
                        listOf(get()),
                        get(),
                        2
                    )
                }
                single<CrossOverOperator> {
                    //EdgeSelectorCrossOver
                    HeuristicCrossOver(get<Task>().costGraph)
                }
                single<Task> {
                    get<TaskLoaderService>().loadTask(folderPath = get(named(FilePathVariableNames.INPUT_FOLDER)))
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
                single<InitializeAlgorithm<EvolutionaryAlgorithmState>> {
                    InitializeEvolutionaryAlgorithm(
                        get(),
                        get()
                    )
                }
                single<InitializePopulation> {
                    InitializePopulationByModuloStepper(32 * 32)
                }
                single<OrderPopulationByCost> { OrderPopulationByCost(get()) }
                single<CalculateCost> { CalculateCostOfTspSolution(get()) }
                single<MutateChildren> { MutateChildrenBySwap }
            }
        )
    }

    val initialization: InitializeAlgorithm<EvolutionaryAlgorithmState> = KoinUtil.get()
    val iteration: AlgorithmIteration<EvolutionaryAlgorithmState> = KoinUtil.get()
    val state = initialization(KoinUtil.get())
    repeat(25_000) {
        iteration(state)
        print("iteration $it.: ")
        println(state.copyOfBest)
    }

    stopKoin()
}