package hu.raven.puppet

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationWithElitistSelection
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationWithElitistSelectionAndModuloStepper
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationWithElitistSelectionAndOneOpposition
import hu.raven.puppet.logic.step.bacterialmutationoperator.*
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.step.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.logic.task.loader.TspTaskLoader
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.parameters.IterativeAlgorithmParameterProvider
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.modules.AlgorithmParameters.*
import hu.raven.puppet.modules.FilePathVariableNames.*
import hu.raven.puppet.utility.get
import hu.raven.puppet.utility.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

//max 13 oldal
private data class Scenario(
    val fileName: String,
    val objectiveCount: Int,
    val mutationStrategy: (
        algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
        parameters: BacterialMutationParameterProvider<Meter>,
        mutationOperator: BacterialMutationOperator<Meter>,
        calculateCostOf: CalculateCost<Meter>,
        selectSegment: SelectSegment<Meter>,
    ) -> MutationOnSpecimen<Meter>,
    val mutationOperator: (
        algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
        parameters: BacterialMutationParameterProvider<Meter>,
    ) -> BacterialMutationOperator<Meter>,
)

private val TASK_SIZES = arrayOf(4, 8, 16, 32, 64, 128, 256, 512, 1024)
private val STRATEGIES: Array<(

    algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
    parameters: BacterialMutationParameterProvider<Meter>,
    mutationOperator: BacterialMutationOperator<Meter>,
    calculateCostOf: CalculateCost<Meter>,
    selectSegment: SelectSegment<Meter>,
) -> MutationOnSpecimen<Meter>> = arrayOf(
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter>,
      mutationOperator: BacterialMutationOperator<Meter>,
      calculateCostOf: CalculateCost<Meter>,
      selectSegment: SelectSegment<Meter> ->

        MutationWithElitistSelection(
            algorithmState,
            parameters,
            mutationOperator,
            calculateCostOf,
            selectSegment
        )
    },
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter>,
      mutationOperator: BacterialMutationOperator<Meter>,
      calculateCostOf: CalculateCost<Meter>,
      selectSegment: SelectSegment<Meter> ->

        MutationWithElitistSelectionAndOneOpposition(
            algorithmState,
            parameters,
            mutationOperator,
            calculateCostOf,
            selectSegment,
        )
    },
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter>,
      mutationOperator: BacterialMutationOperator<Meter>,
      calculateCostOf: CalculateCost<Meter>,
      selectSegment: SelectSegment<Meter> ->

        MutationWithElitistSelectionAndModuloStepper(
            algorithmState,
            parameters,
            mutationOperator,
            calculateCostOf,
            selectSegment
        )
    },
    //other operator for each or more complex
    //random + modulo stepper
)
private val OPERATORS: Array<(

    algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
    parameters: BacterialMutationParameterProvider<Meter>,
) -> BacterialMutationOperator<Meter>> = arrayOf(

    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter> ->

        EdgeBuilderHeuristicOnContinuousSegment(
            algorithmState,
            parameters,
        )
    },
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter> ->

        EdgeBuilderHeuristicOnContinuousSegmentWithWeightRecalculation(
            algorithmState,
            parameters,
        )
    },
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter> ->

        OppositionOperator(
            algorithmState,
            parameters,
        )
    },
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter> ->

        RandomShuffleOfContinuesSegment(
            algorithmState,
            parameters,
        )
    },
    { algorithmState: IterativeAlgorithmStateWithMultipleCandidates<Meter>,
      parameters: BacterialMutationParameterProvider<Meter> ->

        SequentialSelectionHeuristicOnContinuousSegment(
            algorithmState,
            parameters,
        )
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
    output.appendText("scenario: $scenario\n")
    startKoin {
        modules(
            module {
                single(named(CLONE_COUNT)) { 40 }
                single(named(CLONE_SEGMENT_LENGTH)) { scenario.objectiveCount }
                single(named(CLONE_CYCLE_COUNT)) { 5 }
                single(named(SIZE_OF_POPULATION)) { 1 }
                single(named(ITERATION_LIMIT)) { Int.MAX_VALUE }
                single<BacterialMutationParameterProvider<*>> {
                    BacterialMutationParameterProvider<Meter>(
                        cloneCount = 40,
                        cloneSegmentLength = scenario.objectiveCount,
                        cloneCycleCount = 5,
                        algorithmState = get(),
                        sizeOfPopulation = 1,
                        geneCount = scenario.objectiveCount,
                        iterationLimit = Int.MAX_VALUE,
                        mutationPercentage = 0f
                    )
                }
                single<EvolutionaryAlgorithmParameterProvider<*>> {
                    get<BacterialMutationParameterProvider<Meter>>()
                }
                single<IterativeAlgorithmParameterProvider> {
                    get<BacterialMutationParameterProvider<Meter>>()
                }
                single(named(INPUT_FOLDER)) { "input/tsp" }
                single(named(OUTPUT_FOLDER)) { "output" }
                single(named(SINGLE_FILE)) { scenario.fileName }
                single<CalculateCost<*>> {
                    CalculateCostOfTspSolution(
                        get(), get()
                    )
                }
                single { BacterialAlgorithmStatistics() }
                single { DoubleLogger() }
                single<TaskLoader> { TspTaskLoader() }
                single { get<TaskLoader>().loadTask(get(INPUT_FOLDER)) }
                single<BacterialMutationOperator<*>> {
                    scenario.mutationOperator(
                        get(), get()
                    )
                }
                single<SelectSegment<*>> {
                    SelectContinuesSegment<Meter>(
                        get(), get()
                    )
                }
                single<IterativeAlgorithmStateWithMultipleCandidates<*>> {
                    IterativeAlgorithmStateWithMultipleCandidates<Meter>(
                        get()
                    )
                }
                single<AlgorithmState> {
                    get<IterativeAlgorithmStateWithMultipleCandidates<*>>()
                }
            }
        )
    }

    val task: Task by inject()
    val calculateCost: CalculateCost<Meter> by inject()
    task.costGraph.edgesFromCenter.forEach { println(it.length) }
    task.costGraph.edgesToCenter.forEach { println(it.length) }
    task.costGraph.edgesBetween.flatten().forEach { println(it.length) }
    val strategy: MutationOnSpecimen<Meter> = scenario.mutationStrategy(
        get(),
        get(),
        get(),
        get(),
        get(),
    )

    (0 until 25).map {
        val specimen =
            OnePartRepresentation<Meter>(
                0,
                scenario.objectiveCount,
                Permutation((0 until scenario.objectiveCount).toList().toIntArray()),
                true,
                null,
                0,
                0
            )
        strategy(specimen)
        calculateCost(specimen)
        specimen.costOrException()
    }
        .sortedBy { it.value }
        .groupBy { it.value.numerator }
        .forEach { (value, values) ->
            output.appendText("value: $value  count: ${values.size}\n")
        }

    stopKoin()
}