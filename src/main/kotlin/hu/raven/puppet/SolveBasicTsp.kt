package hu.raven.puppet

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.physics.Meter
import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.logic.operator.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.operator.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.step.booststrategy.BoostOnBestLazy
import hu.raven.puppet.logic.step.booststrategy.BoostStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.CrossOverStrategy
import hu.raven.puppet.logic.step.crossoverstrategy.HalfElitistCrossover
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.mutatechildren.MutateChildrenBySwap
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivorsMultyObjectiveHalfElitist
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.*
import hu.raven.puppet.utility.KoinUtil
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File
import java.nio.file.Path

//size 128 instance 0
//[44, 58, 49, 32, 71, 22, 88, 2, 75, 9, 14, 107, 54, 76, 97, 70, 42, 121, 37, 4, 40, 89, 87, 99, 110, 68, 116, 53, 30, 1, 104, 95, 47, 79, 12, 36, 78, 61, 8, 20, 66, 34, 0, 33, 84, 124, 119, 59, 23, 91, 64, 21, 35, 125, 72, 117, 120, 106, 55, 86, 41, 74, 105, 83, 18, 93, 126, 60, 6, 10, 16, 101, 112, 45, 77, 102, 29, 5, 28, 7, 56, 73, 51, 80, 17, 69, 94, 13, 15, 103, 96, 82, 65, 90, 3, 48, 19, 27, 108, 39, 100, 118, 62, 109, 114, 92, 57, 38, 50, 24, 52, 25, 46, 11, 63, 122, 26, 111, 85, 98, 81, 67, 113, 43, 31, 123, 115]
//1767350
//[87, 99, 110, 2, 75, 9, 14, 107, 54, 97, 76, 70, 42, 121, 37, 4, 3, 48, 19, 92, 38, 50, 53, 30, 1, 104, 20, 66, 34, 0, 33, 84, 124, 119, 59, 68, 31, 123, 23, 91, 64, 21, 67, 113, 43, 109, 114, 57, 40, 89, 116, 27, 108, 39, 100, 118, 41, 74, 105, 83, 18, 93, 126, 60, 6, 10, 16, 101, 112, 45, 77, 102, 29, 5, 28, 7, 56, 73, 95, 47, 79, 12, 36, 78, 61, 8, 24, 52, 25, 46, 11, 63, 122, 26, 111, 85, 98, 81, 51, 80, 17, 69, 94, 13, 15, 103, 96, 82, 65, 90, 35, 125, 72, 117, 120, 106, 55, 86, 62, 115, 44, 58, 49, 32, 71, 22, 88]
//1752764

//size 64 instance 0 population 64x64/4
//[8, 28, 25, 42, 24, 34, 1, 13, 9, 54, 6, 10, 39, 48, 43, 16, 38, 31, 56, 0, 19, 5, 59, 15, 40, 26, 29, 20, 53, 4, 37, 55, 41, 27, 18, 57, 14, 47, 50, 45, 12, 36, 7, 46, 52, 33, 3, 17, 49, 21, 11, 23, 62, 30, 58, 22, 44, 35, 60, 32, 51, 2, 61]
//1614266
//[8, 28, 25, 42, 24, 34, 1, 13, 9, 54, 6, 10, 39, 48, 43, 16, 38, 31, 56, 0, 19, 47, 50, 45, 12, 36, 41, 27, 37, 55, 18, 46, 52, 7, 57, 14, 5, 59, 15, 40, 26, 29, 20, 53, 4, 33, 3, 17, 49, 21, 11, 23, 62, 30, 58, 22, 44, 35, 60, 32, 51, 2, 61]
//1643256
//[10, 39, 48, 43, 31, 56, 0, 19, 52, 33, 3, 17, 49, 13, 14, 15, 40, 26, 5, 59, 47, 50, 45, 12, 36, 41, 27, 7, 46, 25, 42, 24, 34, 1, 29, 20, 53, 4, 37, 55, 58, 18, 16, 38, 57, 9, 54, 6, 21, 11, 23, 62, 30, 22, 44, 35, 60, 32, 51, 2, 61, 8, 28]
//1650433
//size 64 instance 0 population 64x64
//1608749
//[8, 28, 25, 42, 24, 34, 57, 14, 21, 45, 12, 36, 7, 47, 50, 46, 52, 33, 3, 17, 49, 1, 13, 9, 54, 6, 10, 39, 48, 43, 16, 38, 31, 56, 0, 19, 5, 59, 15, 40, 26, 29, 20, 53, 4, 37, 55, 41, 27, 11, 23, 62, 30, 18, 58, 22, 44, 35, 60, 32, 51, 2, 61]
//1599378
//[8, 28, 25, 42, 24, 34, 1, 29, 20, 53, 4, 7, 52, 33, 3, 17, 49, 13, 9, 54, 6, 10, 14, 21, 11, 23, 62, 30, 18, 57, 39, 48, 43, 16, 38, 31, 56, 0, 19, 5, 59, 15, 40, 26, 47, 46, 50, 45, 12, 36, 41, 27, 37, 55, 58, 22, 44, 35, 60, 32, 51, 2, 61]
fun main() {
    for (i in 0..99) {
        runOnInstance(i)
    }
}

fun runOnInstance(instanceId: Int) {
    val SIZE = 64
    startKoin {
        modules(module {
            single(named(FilePathVariableNames.INPUT_FOLDER)) { "\\input\\tsp$SIZE" }
            single(named(FilePathVariableNames.SINGLE_FILE)) { "instance$instanceId.json" }
            single(named(FilePathVariableNames.OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
            single<InitializeAlgorithm<*>> {
                InitializeEvolutionaryAlgorithm(
                    initializePopulation = get(),
                    orderPopulationByCost = get()
                )
            }
            single<InitializePopulation> {
                InitializePopulationByModuloStepper(SIZE * SIZE)
            }
            single {
                OrderPopulationByCost(calculateCostOf = get())
            }
            single<CalculateCost> {
                CalculateCostOfTspSolution(task = get())
            }
            single<ObjectLoggerService<*>> {
                ObjectLoggerService<String>(outputPath = get(named(FilePathVariableNames.OUTPUT_FOLDER)))
            }
            single<Task> {
                loadTask(
                    get(named(FilePathVariableNames.INPUT_FOLDER)),
                    get(named(FilePathVariableNames.SINGLE_FILE)),
                )
            }
            single<AlgorithmIteration<*>> {
                EvolutionaryAlgorithmIteration(
                    steps = arrayOf(
                        get<BoostStrategy>(),
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
                Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
                    calculateCostOf = get(),
                    SIZE,
                    SIZE * SIZE,
                    SIZE - 1
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
                MutateChildrenBySwap
            }
        })
    }

    val initialization: InitializeAlgorithm<EvolutionaryAlgorithmState> = KoinUtil.get()
    val iteration: AlgorithmIteration<EvolutionaryAlgorithmState> = KoinUtil.get()
    val state = initialization(KoinUtil.get())
    repeat(10_000) {
        iteration(state)
        if (it % 100 == 0) {
            print("iteration $it.: ")
            println(state.copyOfBest)
        }
    }

    state.copyOfBest?.permutation?.toIntArray()?.let {
        File("results").appendText("\n${it.asList()}")
    }

    stopKoin()
}

private fun loadTask(inputFolder: String, inputFile: String): Task {
    val matrix = MatrixLoader.loadMatrix(inputFolder, inputFile)
    val costGraph = CostGraph(
        center = Gps(),
        objectives = Array(matrix.size - 1) { CostGraphVertex() }.asImmutable(),
        edgesFromCenter = matrix[0]
            .slice(1..<matrix[0].size)
            .map { CostGraphEdge(length = Meter(it)) }
            .toTypedArray()
            .asImmutable(),
        edgesToCenter = matrix
            .slice(1 until matrix.size)
            .map { CostGraphEdge(length = Meter(it[0])) }
            .toTypedArray()
            .asImmutable(),
        edgesBetween = matrix
            .slice(1 until matrix.size)
            .asSequence()
            .map { it.slice(1..<it.size) }
            .map { it.filter { it != 0 } }
            .map { it.map { CostGraphEdge(length = Meter(it)) } }
            .map { it.toTypedArray() }
            .map { it.asImmutable() }
            .toList()
            .toTypedArray()
            .asImmutable()
    )
    return Task(
        transportUnits = ImmutableArray.immutableArrayOf(),
        costGraph = costGraph
    )
}

class MatrixLoader {
    companion object {

        fun loadMatrix(inputFolder: String, inputFile: String): List<List<Int>> {
            val url = this::class.java.getResource("$inputFolder\\$inputFile".replace("\\", "/"))
                ?: throw Exception("File not found")
            return url.readText()
                .split("\n")
                .map {
                    it.split("\t").map { it.toInt() }
                }
                .toList()
        }
    }
}