package hu.raven.puppet.job.experiments

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
import hu.raven.puppet.logic.operator.boost_operator.SimplifiedTwoOptStepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostWithLogging
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.logic.operator.crossover_operator.HeuristicCrossOver
import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferByCrossOver
import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
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
import hu.raven.puppet.logic.step.gene_transfer.GeneTransfer
import hu.raven.puppet.logic.step.gene_transfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivors
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivorsMultiObjectiveHalfElitist
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspFromMatrixTaskLoaderService
import hu.raven.puppet.model.logging.LogType
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.state.BacteriophageAlgorithmStateForLogging
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.MutableCompleteGraph
import hu.raven.puppet.utility.extention.KoinUtil
import hu.raven.puppet.utility.extention.KoinUtil.get
import hu.raven.puppet.utility.extention.toMutable
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.LocalDate
import java.time.LocalDateTime

private typealias TspTask2 = CompleteGraph<Unit, Int>

private data class BacteriophageAlgorithmConfiguration2(
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

    val injectionCount: Int,
    val geneTransferSegmentLength: Int,
)

fun main() {
    (2 until 10).forEach { taskInstanceIndex ->
        //arrayOf(4, 8, 16, 32, 64, 128, 256).forEach { sizeOfPopulation ->
        arrayOf(64).forEach { sizeOfPopulation ->
            //arrayOf(4, 16, 64, 128).forEach { sizeOfBacteriophagePopulation ->
            arrayOf(64).forEach { sizeOfBacteriophagePopulation ->
                arrayOf(2, 4, 8, 16, 32).forEach { cloneCount ->
                    arrayOf(2, 4, 8, 16, 32).forEach { cloneCycleCount ->
                        arrayOf(0f, 1f / 8, 1f / 4, 1f / 2, 1f).forEach { mutationPercentage ->
                            //arrayOf(0f, 1f / 8, 1f / 4, 1f / 2, 1f).forEach { infectionRate ->
                            arrayOf(1f).forEach { infectionRate ->
                                //arrayOf(1f, 7f / 8, 3f / 4, 1f / 2).forEach { lifeCoefficient ->
                                arrayOf(0.5f).forEach { lifeCoefficient ->
                                    //arrayOf(1f, 7f / 8, 3f / 4, 1f / 2).forEach { lifeReductionRate ->
                                    arrayOf(0.5f).forEach { lifeReductionRate ->
                                        //arrayOf(0f, 1f / 8, 1f / 4, 1f / 2, 1f).forEach { boostLuckyRate ->
                                        arrayOf(1.125f).forEach { boostLuckyRate ->
                                            val boostLuckyCount = (sizeOfPopulation * boostLuckyRate).toInt()
                                            //arrayOf(8, 16, 32, 64, 128, 256).forEach { boostStepLimit ->
                                            arrayOf(64).forEach { boostStepLimit ->
                                                //arrayOf(4, 16, 64).forEach inner@{ injectionCount ->
                                                arrayOf(4).forEach inner@{ injectionCount ->
                                                    if(
                                                        taskInstanceIndex < 3 ||
                                                        taskInstanceIndex == 3 && cloneCount < 32 ||
                                                        taskInstanceIndex == 3 && cloneCount == 32 && cloneCycleCount < 32 ||
                                                        taskInstanceIndex == 3 && cloneCount == 32 && cloneCycleCount == 32 && mutationPercentage < 0.5 ||
                                                        taskInstanceIndex == 3 && cloneCount == 32 && cloneCycleCount == 32 && mutationPercentage == 0.5f
                                                    ) return@inner

                                                    BacteriophageAlgorithmConfiguration2(
                                                        fileName = "instance$taskInstanceIndex.json",
                                                        inputFolder = "D:\\Research\\Datasets\\tsp64x10_000",
                                                        outputFolder = listOf(
                                                            "D:", "Research", "Results3", "${LocalDate.now()}",
                                                            LocalDateTime.now().toString()
                                                                .replace(":", "_")
                                                                .replace(".", "_")
                                                        ),

                                                        sizeOfPopulation = sizeOfPopulation,
                                                        sizeOfBacteriophagePopulation = sizeOfBacteriophagePopulation,
                                                        sizeOfPermutation = 63,

                                                        cloneCount = cloneCount,
                                                        cloneCycleCount = cloneCycleCount,
                                                        cloneSegmentLength = 4,
                                                        mutationPercentage = mutationPercentage,

                                                        infectionRate = infectionRate,
                                                        lifeCoefficient = lifeCoefficient,
                                                        lifeReductionRate = lifeReductionRate,

                                                        boostLuckyCount = boostLuckyCount,
                                                        boostStepLimit = boostStepLimit,

                                                        injectionCount = injectionCount,
                                                        geneTransferSegmentLength = 63,

                                                        iterationLimit = 2_000,
                                                    ).let(::runBacteriophage)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun runBacteriophage(configuration: BacteriophageAlgorithmConfiguration2) {
    startKoin {
        modules(
            module {
                single { configuration }
            },
            module {
                single<InitializeAlgorithm<*, *>> {
                    InitializeBacteriophageAlgorithm(
                        InitializeEvolutionaryAlgorithm<TspTask2>(
                            initializePopulation = get(),
                            orderPopulationByCost = get()
                        ),
                        get()
                    )
                }
                single<InitializePopulation> {
                    InitializePopulationByModuloStepper(
                        sizeOfPopulation = get<BacteriophageAlgorithmConfiguration2>().sizeOfPopulation,
                        sizeOfTask = get<BacteriophageAlgorithmConfiguration2>().sizeOfPermutation
                    )
                }
                single<InitializeBacteriophagePopulation> {
                    BasicInitializationOfBacteriophagePopulation(get<BacteriophageAlgorithmConfiguration2>().sizeOfBacteriophagePopulation)
                }
                single {
                    OrderPopulationByCost<TspTask2>(calculateCostOf = get())
                }
                single<CalculateCost<*>> {
                    CalculateCostWithLogging(
                        classOfSolutionRepresentation = OnePartRepresentationWithCostAndIterationAndId::class.java,
                        calculateCost = CalculateCostOfTspSolution(task = get<TspTask2>()),
                        loggingChannel = get(named("cost")),
                        task = get<TspTask2>()
                    )
                }
                single<TaskLoaderService<TspTask2>> {
                    TspFromMatrixTaskLoaderService(
                        log = { println(it) },
                        fileName = get<BacteriophageAlgorithmConfiguration2>().fileName
                    )
                }
                single {
                    get<TaskLoaderService<TspTask2>>().loadTask(folderPath = get<BacteriophageAlgorithmConfiguration2>().inputFolder)
                }
                single<AlgorithmIteration<BacteriophageAlgorithmState<TspTask2>>> {
                    val stateToSerializableMapper = { state: BacteriophageAlgorithmState<TspTask2> ->
                        BacteriophageAlgorithmStateForLogging(
                            population = state.population.activesAsSequence().toList(),
                            virusPopulation = state.virusPopulation.activesAsSequence().toList(),
                            iteration = state.iteration
                        )
                    }

                    val wrapIntoLogger =
                        { state: EvolutionaryAlgorithmStep<BacteriophageAlgorithmState<TspTask2>> ->
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
                            get<BacteriophageTranscription<TspTask2>>().let(wrapIntoLogger),
                            get<OrderPopulationByCost<TspTask2>>().let(wrapIntoLogger),
                            get<BoostStrategy>().let(wrapIntoLogger),
                        )
                    )
                }
                single<SelectSurvivors> {
                    SelectSurvivorsMultiObjectiveHalfElitist
                }
                single<BacterialMutation> {
                    BacterialMutationOnBestAndLuckyByShuffling(
                        get(),
                        get<BacteriophageAlgorithmConfiguration2>().mutationPercentage
                    )
                }
                single<MutationOnSpecimen> {
                    MutationOnSpecimenWithBacteriophageTransduction(
                        MutationWithElitistSelection(
                            get(),
                            get(),
                            get(),
                            cloneCount = get<BacteriophageAlgorithmConfiguration2>().cloneCount,
                            cloneCycleCount = get<BacteriophageAlgorithmConfiguration2>().cloneCycleCount
                        ),
                        get(),
                        KoinUtil::get
                    )
                }
                single<BacteriophageAlgorithmState<TspTask2>> {
                    get<InitializeAlgorithm<TspTask2, BacteriophageAlgorithmState<TspTask2>>>()(
                        get()
                    )
                }
                single { BacteriophageTransductionOperator() }
                single<BacterialMutationOperator> { EdgeBuilderHeuristicOnContinuousSegment(get(), Int::toFloat) }
                single<SelectSegments> { SelectCuts(cloneSegmentLength = get<BacteriophageAlgorithmConfiguration2>().cloneSegmentLength) }

                single<GeneTransfer> {
                    GeneTransferByTournament(
                        injectionCount = get<BacteriophageAlgorithmConfiguration2>().injectionCount,
                        geneTransferOperator = get()
                    )
                }

                single<GeneTransferOperator<*>> {
                    GeneTransferByCrossOver<TspTask2>(
                        calculateCostOf = get(),
                        geneTransferSegmentLength = get<BacteriophageAlgorithmConfiguration2>().geneTransferSegmentLength,
                        crossOverOperator = get()
                    )
                }

                single<CrossOverOperator> {
                    HeuristicCrossOver(
                        costGraph = get(),
                        extractEdgeCost = Int::toFloat
                    )
                }

                single<BacteriophageTranscription<TspTask2>> {
                    BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion(
                        get<BacteriophageAlgorithmConfiguration2>().infectionRate,
                        get<BacteriophageAlgorithmConfiguration2>().lifeReductionRate,
                        get<BacteriophageAlgorithmConfiguration2>().lifeCoefficient,
                        get(),
                        get(),
                        Int::toFloat
                    )
                }
                single<BoostStrategy> {
                    BoostOnBestAndLucky(
                        luckyCount = get<BacteriophageAlgorithmConfiguration2>().boostLuckyCount,
                        boostOperator = get()
                    )
                }
                single<BoostOperator<*>> {
                    BoostOperatorWithBacteriophageTransduction(
                        SimplifiedTwoOptStepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
                            get(),
                            stepLimit = get<BacteriophageAlgorithmConfiguration2>().boostStepLimit,
                            populationSize = get<BacteriophageAlgorithmConfiguration2>().sizeOfPopulation,
                            permutationSize = get<BacteriophageAlgorithmConfiguration2>().sizeOfPermutation
                        ),
                        get(),
                        KoinUtil::get
                    )
                }

                single<LoggingChannel<*>>(named("state")) {
                    JsonChannel<BacteriophageAlgorithmStateForLogging>(
                        outputFolder = get<BacteriophageAlgorithmConfiguration2>().outputFolder,
                        outputFileName = "algorithmState",
                        type = LogType.INFO,
                        name = "perStepStateLogger",
                        version = 1,
                    )
                }

                single<LoggingChannel<*>>(named("cost")) {
                    JsonChannel<Pair<OnePartRepresentationWithCostAndIterationAndId, List<Float>>>(
                        outputFolder = get<BacteriophageAlgorithmConfiguration2>().outputFolder,
                        outputFileName = "cost",
                        type = LogType.INFO,
                        name = "perCostCallLogger",
                        version = 1,
                    )
                }

                single<LoggingChannel<*>>(named("task")) {
                    JsonChannel<MutableCompleteGraph<Unit, Int>>(
                        outputFolder = get<BacteriophageAlgorithmConfiguration2>().outputFolder,
                        outputFileName = "task",
                        type = LogType.INFO,
                        name = "taskLogger",
                        version = 1,
                    )
                }

                single<LoggingChannel<*>>(named("config")) {
                    JsonChannel<BacteriophageAlgorithmConfiguration2>(
                        outputFolder = get<BacteriophageAlgorithmConfiguration2>().outputFolder,
                        outputFileName = "config",
                        type = LogType.INFO,
                        name = "configLogger",
                        version = 1,
                    )
                }
            }
        )
    }

    val iteration: AlgorithmIteration<BacteriophageAlgorithmState<TspTask2>> = get()
    val algorithmState: BacteriophageAlgorithmState<TspTask2> = get()

    get<LoggingChannel<BacteriophageAlgorithmConfiguration2>>("config")
        .apply { initialize() }
        .send(get<BacteriophageAlgorithmConfiguration2>())
    get<LoggingChannel<MutableCompleteGraph<Unit, Int>>>("task")
        .apply { initialize() }
        .send(get<TspTask2>().toMutable())

    repeat(get<BacteriophageAlgorithmConfiguration2>().iterationLimit) {
        iteration(algorithmState)
        if (it % 100 == 0)
            println("iteration $it.: ${algorithmState.copyOfBest}")
    }

    stopKoin()
}