package hu.raven.puppet

import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeBacteriophageAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.JsonChannel
import hu.raven.puppet.logic.logging.LoggingChannel
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimenWithBacteriophageTransduction
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationWithElitistSelection
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.EdgeBuilderHeuristicOnContinuousSegment
import hu.raven.puppet.logic.operator.bacteriophage_transduction_operator.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.logic.operator.boost_operator.BoostOperatorWithBacteriophageTransduction
import hu.raven.puppet.logic.operator.boost_operator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostWithLogging
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.BasicInitializationOfBacteriophagePopulation
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.InitializeBacteriophagePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.operator.select_segments.SelectCuts
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.StepLogger
import hu.raven.puppet.logic.step.bacterial_mutation.BacterialMutation
import hu.raven.puppet.logic.step.bacterial_mutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.bacteriophage_transcription.BacteriophageTranscription
import hu.raven.puppet.logic.step.bacteriophage_transcription.BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion
import hu.raven.puppet.logic.step.boost_strategy.BoostOnBestAndLucky
import hu.raven.puppet.logic.step.boost_strategy.BoostStrategy
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivors
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivorsMultiObjectiveHalfElitist
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspFromMatrixTaskLoaderService
import hu.raven.puppet.model.logging.LogType
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.state.BacteriophageAlgorithmStateForLogging
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.utility.extention.KoinUtil
import hu.raven.puppet.utility.extention.KoinUtil.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.LocalDate
import java.time.LocalDateTime

private typealias Task = CompleteGraph<Unit, Int>

private data class Configuration(
    val fileName: String,
    val inputFolder: String,
    val outputFolder: List<String>,

    val sizeOfPopulation: Int,
    val sizeOfBacteriophagePopulation: Int,
    val sizeOfPermutation: Int,

    val cloneCount: Int,
    val cloneCycleCount: Int,
    val cloneSegmentLength: Int,
    val mutationPercentage: Float,

    val infectionRate: Float,
    val lifeReductionRate: Float,
    val lifeCoefficient: Float,

    val boostLuckyCount: Int,
    val boostStepLimit: Int,
    val iterationLimit: Int,
)

fun main() {
    startKoin {
        modules(
            module {
                single {
                    Configuration(
                        fileName = "instance1.json",
                        inputFolder = "\\input\\tsp64",
                        outputFolder = listOf(
                            "output", "${LocalDate.now()}",
                            LocalDateTime.now().toString()
                                .replace(":", "_")
                                .replace(".", "_")
                        ),

                        sizeOfPopulation = 64 * 4,
                        sizeOfBacteriophagePopulation = 64,
                        sizeOfPermutation = 63,

                        cloneCount = 2,
                        cloneCycleCount = 32,
                        cloneSegmentLength = 4,
                        mutationPercentage = 1f,

                        infectionRate = 1 / 8f,
                        lifeCoefficient = 0.5f,
                        lifeReductionRate = 0.5f,

                        boostLuckyCount = 64,
                        boostStepLimit = 64 * 4,

                        iterationLimit = 10_000,
                    )
                }
            },
            module {
                single<InitializeAlgorithm<*, *>> {
                    InitializeBacteriophageAlgorithm(
                        InitializeEvolutionaryAlgorithm<Task>(
                            initializePopulation = get(),
                            orderPopulationByCost = get()
                        ),
                        get()
                    )
                }
                single<InitializePopulation> {
                    InitializePopulationByModuloStepper(
                        sizeOfPopulation = get<Configuration>().sizeOfPopulation,
                        sizeOfTask = get<Configuration>().sizeOfPermutation
                    )
                }
                single<InitializeBacteriophagePopulation> {
                    BasicInitializationOfBacteriophagePopulation(get<Configuration>().sizeOfBacteriophagePopulation)
                }
                single {
                    OrderPopulationByCost<Task>(calculateCostOf = get())
                }
                single<CalculateCost<*>> {
                    CalculateCostWithLogging(
                        classOfSolutionRepresentation = OnePartRepresentationWithCostAndIterationAndId::class.java,
                        calculateCost = CalculateCostOfTspSolution(task = get<Task>()),
                        loggingChannel = get(named("cost")),
                        task = get<Task>()
                    )
                }
                single<TaskLoaderService<Task>> {
                    TspFromMatrixTaskLoaderService(
                        log = { println(it) },
                        fileName = get<Configuration>().fileName
                    )
                }
                single {
                    get<TaskLoaderService<Task>>().loadTask(folderPath = get<Configuration>().inputFolder)
                }
                single<AlgorithmIteration<BacteriophageAlgorithmState<Task>>> {
                    val stateToSerializableMapper = { state: BacteriophageAlgorithmState<Task> ->
                        BacteriophageAlgorithmStateForLogging(
                            population = state.population.activesAsSequence().toList(),
                            virusPopulation = state.virusPopulation.activesAsSequence().toList(),
                            iteration = state.iteration
                        )
                    }

                    val wrapIntoLogger =
                        { state: EvolutionaryAlgorithmStep<BacteriophageAlgorithmState<Task>> ->
                            StepLogger(
                                state,
                                get(named("state")),
                                stateToSerializableMapper
                            )
                        }

                    EvolutionaryAlgorithmIteration(
                        steps = arrayOf(
                            get<SelectSurvivors>().let(wrapIntoLogger),
                            get<BacterialMutation>().let(wrapIntoLogger),
                            get<BacteriophageTranscription<Task>>().let(wrapIntoLogger),
                            get<OrderPopulationByCost<Task>>().let(wrapIntoLogger),
                            get<BoostStrategy>().let(wrapIntoLogger),
                        )
                    )
                }
                single<SelectSurvivors> {
                    SelectSurvivorsMultiObjectiveHalfElitist
                }
                single<BacterialMutation> {
                    BacterialMutationOnBestAndLuckyByShuffling(get(), get<Configuration>().mutationPercentage)
                }
                single<MutationOnSpecimen> {
                    MutationOnSpecimenWithBacteriophageTransduction(
                        MutationWithElitistSelection(
                            get(),
                            get(),
                            get(),
                            cloneCount = get<Configuration>().cloneCount,
                            cloneCycleCount = get<Configuration>().cloneCycleCount
                        ),
                        get(),
                        KoinUtil::get
                    )
                }
                single<BacteriophageAlgorithmState<Task>> {
                    get<InitializeAlgorithm<Task, BacteriophageAlgorithmState<Task>>>()(
                        get()
                    )
                }
                single { BacteriophageTransductionOperator() }
                single<BacterialMutationOperator> { EdgeBuilderHeuristicOnContinuousSegment(get(), Int::toFloat) }
                single<SelectSegments> { SelectCuts(cloneSegmentLength = get<Configuration>().cloneSegmentLength) }
                single<BacteriophageTranscription<Task>> {
                    BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion(
                        get<Configuration>().infectionRate,
                        get<Configuration>().lifeReductionRate,
                        get<Configuration>().lifeCoefficient,
                        get(),
                        get(),
                        Int::toFloat
                    )
                }
                single<BoostStrategy> {
                    BoostOnBestAndLucky(
                        luckyCount = get<Configuration>().boostLuckyCount,
                        boostOperator = get()
                    )
                }
                single<BoostOperator<*>> {
                    BoostOperatorWithBacteriophageTransduction(
                        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
                            get(),
                            stepLimit = get<Configuration>().boostStepLimit,
                            populationSize = get<Configuration>().sizeOfPopulation,
                            permutationSize = get<Configuration>().sizeOfPermutation
                        ),
                        get(),
                        KoinUtil::get
                    )
                }
                single<LoggingChannel<*>>(named("state")) {
                    JsonChannel<BacteriophageAlgorithmState<Task>>(
                        outputFolder = get<Configuration>().outputFolder,
                        outputFileName = "algorithmState",
                        type = LogType.INFO,
                        name = "perStepStateLogger",
                        version = 1,
                    )
                }
                single<LoggingChannel<*>>(named("cost")) {
                    JsonChannel<Pair<OnePartRepresentation, List<Float>>>(
                        outputFolder = get<Configuration>().outputFolder,
                        outputFileName = "cost",
                        type = LogType.INFO,
                        name = "perCostCallLogger",
                        version = 1,
                    )
                }
            }
        )
    }

    val iteration: AlgorithmIteration<BacteriophageAlgorithmState<Task>> = get()
    val algorithmState: BacteriophageAlgorithmState<Task> = get()

    repeat(get<Configuration>().iterationLimit) {
        iteration(algorithmState)
        println("iteration $it.: ${algorithmState.copyOfBest}")
    }

    stopKoin()
}