package hu.raven.puppet

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationWithElitistSelection
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationWithElitistSelectionAndModuloStepper
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationWithElitistSelectionAndOneOpposition
import hu.raven.puppet.logic.operator.bacterialmutationoperator.*
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.logic.operator.selectsegments.SelectSingleValuesContinuouslyWithFullCoverage
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.modules.AlgorithmParameters.*
import hu.raven.puppet.modules.FilePathVariableNames.*
import hu.raven.puppet.utility.KoinUtil.get
import hu.raven.puppet.utility.extention.FloatArrayExtensions.vectorLength
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File
import java.time.LocalDateTime
import kotlin.io.path.Path

//TODO:
// other operator for each or more complex
// random + modulo stepper
// bigger examples
private data class Scenario(
    val fileName: String,
    val objectiveCount: Int,
    val mutationStrategy: (
        mutationOperator: BacterialMutationOperator,
        calculateCostOf: CalculateCost,
        selectSegments: SelectSegments,
        cloneCount: Int,
        cloneCycleCount: Int,
    ) -> MutationOnSpecimen,
    val mutationOperator: (task: Task) -> BacterialMutationOperator,
)

private val TASK_SIZES = arrayOf(4, 8, 16, 32, 64, 128, 256, 512, 1024)
private val STRATEGIES: Array<(
    mutationOperator: BacterialMutationOperator,
    calculateCostOf: CalculateCost,
    selectSegments: SelectSegments,
    cloneCount: Int,
    cloneCycleCount: Int
) -> MutationOnSpecimen> = arrayOf(
    { mutationOperator: BacterialMutationOperator,
      calculateCostOf: CalculateCost,
      selectSegments: SelectSegments,
      cloneCount: Int,
      cloneCycleCount: Int ->

        MutationWithElitistSelection(
            mutationOperator,
            calculateCostOf,
            selectSegments,
            cloneCount,
            cloneCycleCount
        )
    },
    { mutationOperator: BacterialMutationOperator,
      calculateCostOf: CalculateCost,
      selectSegments: SelectSegments,
      cloneCount: Int,
      cloneCycleCount: Int ->

        MutationWithElitistSelectionAndOneOpposition(
            mutationOperator,
            calculateCostOf,
            selectSegments,
            cloneCount,
            cloneCycleCount
        )
    },
    { mutationOperator: BacterialMutationOperator,
      calculateCostOf: CalculateCost,
      selectSegments: SelectSegments,
      cloneCount: Int,
      cloneCycleCount: Int ->

        MutationWithElitistSelectionAndModuloStepper(
            mutationOperator,
            calculateCostOf,
            selectSegments,
            cloneCount,
            cloneCycleCount,
            determinismRatio = 0.1f
        )
    },
)
private val OPERATORS: Array<(
    task: Task
) -> BacterialMutationOperator> = arrayOf(
    { task: Task ->
        EdgeBuilderHeuristicOnContinuousSegment(task)
    },
    { task: Task ->
        EdgeBuilderHeuristicOnContinuousSegmentWithWeightRecalculation(task)
    },
    { OppositionOperator },
    { RandomShuffleOperator },
    { task: Task ->
        SequentialSelectionHeuristicOnContinuousSegment(task)
    }
)

private val output by lazy { File("output/tspStat.txt") }

fun main() {
    output.writeText("")
    TASK_SIZES.forEach { taskSize ->
        repeat(10) { instanceIndex ->
            STRATEGIES.forEach { strategySupplier ->
                OPERATORS.forEach { operatorSupplier ->
                    runScenario(
                        Scenario(
                            "size${taskSize}instance${instanceIndex}.json",
                            taskSize - 1,
                            strategySupplier,
                            operatorSupplier
                        )
                    )
                }
            }
        }
    }

}

private fun runScenario(scenario: Scenario) {
    startKoin {
        modules(
            module {
                single(named(CLONE_COUNT)) { scenario.objectiveCount * 5 }
                single(named(CLONE_SEGMENT_LENGTH)) { scenario.objectiveCount / 2 + 1 }
                single(named(CLONE_CYCLE_COUNT)) { 5 }
                single(named(SIZE_OF_POPULATION)) { 1 }
                single(named(ITERATION_LIMIT)) { Int.MAX_VALUE }
                single(named(INPUT_FOLDER)) { "\\input\\tsp" }
                single(named(OUTPUT_FOLDER)) { "output" }
                single(named(SINGLE_FILE)) { scenario.fileName }
                single<CalculateCost> {
                    CalculateCostOfTspSolution(
                        get()
                    )
                }
                single {
                    ObjectLoggerService<String>(
                        Path(
                            get(OUTPUT_FOLDER),
                            LocalDateTime.now().toString()
                                .replace(":", "-")
                                .replace(".", "-")
                                .let { "statistics-$it.txt" }
                        )
                    )
                }
                single<TaskLoaderService> { TspTaskLoaderService(get(), get(SINGLE_FILE)) }
                single { get<TaskLoaderService>().loadTask(get(INPUT_FOLDER)) }
                single<BacterialMutationOperator> {
                    scenario.mutationOperator(
                        get(),
                    )
                }
                single<SelectSegments> {
                    //TODO
                    SelectSingleValuesContinuouslyWithFullCoverage(
                        get(CLONE_SEGMENT_LENGTH)
                    )
                }
                single<AlgorithmState> {
                    get<EvolutionaryAlgorithmState>()
                }
            }
        )
    }

//    val task: Task = get()
    val calculateCost: CalculateCost = get()
//    task.costGraph.edgesFromCenter.forEach { println(it.length) }
//    task.costGraph.edgesToCenter.forEach { println(it.length) }
//    task.costGraph.edgesBetween.flatten().forEach { println(it.length) }
    arrayOf(arrayOf(0)).flatten()
    val strategy: MutationOnSpecimen = scenario.mutationStrategy(
        get(),
        get(),
        get(),
        get(CLONE_COUNT),
        get(CLONE_CYCLE_COUNT),
    )
    output.appendText("scenario: ${scenario.fileName} ${scenario.objectiveCount} ${strategy::class} ${get<BacterialMutationOperator>()::class}\n")

    runBlocking(Dispatchers.Default) {
        launch {
            (0 ..<25)
                .map {
                    async {
                        val specimen =
                            OnePartRepresentationWithCostAndIterationAndId(
                                0,
                                0,
                                null,
                                scenario.objectiveCount,
                                Permutation((0 ..<scenario.objectiveCount).toList().toIntArray())
                            )
                        strategy(IndexedValue(0, specimen), 0)
                        calculateCost(specimen)
                        specimen.costOrException()
                    }
                }
                .map { it.await() }
                .asSequence()
                .sortedBy { it.vectorLength() }
                .groupBy { it }
                .forEach { (value, values) ->
                    output.appendText("value: $value  count: ${values.size}\n")
                }
        }
    }

    stopKoin()
}