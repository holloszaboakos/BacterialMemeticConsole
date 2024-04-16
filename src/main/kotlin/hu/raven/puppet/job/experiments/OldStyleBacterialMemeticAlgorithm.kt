package hu.raven.puppet.job.experiments

import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.JsonChannel
import hu.raven.puppet.logic.logging.LoggingChannel
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationWithElitistSelection
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.EdgeBuilderHeuristicOnContinuousSegment
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.logic.operator.boost_operator.SimplifiedTwoOptStepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostWithLogging
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.logic.operator.crossover_operator.HeuristicCrossOver
import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferByCrossOver
import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.operator.select_segments.SelectCuts
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.StepLogger
import hu.raven.puppet.logic.step.bacterial_mutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.boost_strategy.BoostOnBestAndLucky
import hu.raven.puppet.logic.step.gene_transfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.logic.task.loader.TspFromMatrixTaskLoaderService
import hu.raven.puppet.model.logging.LogType
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.state.EvolutionaryAlgorithmStateForLogging
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.MutableCompleteGraph
import hu.raven.puppet.utility.extention.KoinUtil.get
import hu.raven.puppet.utility.extention.toMutable
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.LocalDate
import java.time.LocalDateTime

typealias Task = CompleteGraph<Unit, Int>

fun main() {
//    (0..<10).forEach { instanceIdnex ->
//        arrayOf(8, 16, 32, 64).forEach { sizeOfPopulation ->
//            arrayOf(2, 4).forEach { cloneCount ->
//                val cloneCycleCount = 64 / cloneCount
//                arrayOf(4).forEach { cloneSegmentLength ->
//                    arrayOf(1f / 32, 0f).forEach { mutationPercentage ->
//                        arrayOf(4, 8, 16).forEach { injectionCount ->
//                            arrayOf(4, 8).forEach { boostLuckyCount ->
//                                arrayOf(16).forEach inner@{ boostStepLimit ->
                                    runBacterial(
                                        Configuration(
                                            fileName = "instance8.json",
                                            inputFolder = "D:\\Research\\Datasets\\tsp64x10_000",
                                            outputFolder = listOf(
                                                "D:", "Research","Results2", "${LocalDate.now()}",
                                                LocalDateTime.now().toString()
                                                    .replace(":", "_")
                                                    .replace(".", "_")
                                            ),

                                            sizeOfPopulation = 16,
                                            sizeOfPermutation = 63,

                                            cloneCount = 4,
                                            cloneCycleCount = 16,
                                            cloneSegmentLength = 4,
                                            mutationPercentage = 0.5f,

                                            injectionCount = 16,
                                            geneTransferSegmentLength = 63,

                                            boostLuckyCount = 2,
                                            boostStepLimit = 64,

                                            iterationLimit = 20_000,
                                        )
                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}


data class Configuration(
    val fileName: String,
    val inputFolder: String,
    val outputFolder: List<String>,

    val sizeOfPopulation: Int,
    val sizeOfPermutation: Int,

    val cloneCount: Int,
    val cloneCycleCount: Int,
    val cloneSegmentLength: Int,
    val mutationPercentage: Float,

    val boostLuckyCount: Int,
    val boostStepLimit: Int,
    val iterationLimit: Int,

    val injectionCount: Int,
    val geneTransferSegmentLength: Int,
)

private fun runBacterial(configuration: Configuration) {
    startKoin {
        modules(
            module {
                single { configuration }

                single {
                    InitializeEvolutionaryAlgorithm<Task>(
                        initializePopulation = get(),
                        orderPopulationByCost = get(),
                    )
                }

                single<InitializePopulation> {
                    InitializePopulationByModuloStepper(
                        sizeOfPopulation = get<Configuration>().sizeOfPopulation,
                        sizeOfTask = get<Configuration>().sizeOfPermutation
                    )
                }

                single {
                    OrderPopulationByCost<Task>(
                        calculateCostOf = get()
                    )
                }

                single<CalculateCost<*>> {
                    CalculateCostWithLogging(
                        classOfSolutionRepresentation = OnePartRepresentationWithCostAndIterationAndId::class.java,
                        calculateCost = CalculateCostOfTspSolution(task = get<Task>()),
                        loggingChannel = get(named("cost")),
                        task = get<Task>()
                    )
                }

                single<Task> {
                    TspFromMatrixTaskLoaderService(
                        fileName = get<Configuration>().fileName,
                        log = { println(it) }
                    )
                        .loadTask(get<Configuration>().inputFolder)
                }

                single {
                    val stateToSerializableMapper = { state: EvolutionaryAlgorithmState<Task> ->
                        EvolutionaryAlgorithmStateForLogging(
                            population = state.population.activesAsSequence().toList(),
                            iteration = state.iteration
                        )
                    }

                    val wrapIntoLogger =
                        { state: EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState<Task>> ->
                            StepLogger(
                                state,
                                get(named("state")),
                                stateToSerializableMapper
                            )
                        }

                    EvolutionaryAlgorithmIteration(
                        steps = arrayOf(
                            BacterialMutationOnBestAndLuckyByShuffling(
                                mutationOnSpecimen = get(),
                                mutationPercentage = get<Configuration>().mutationPercentage
                            )
                                .let(wrapIntoLogger),
                            GeneTransferByTournament(
                                injectionCount = get<Configuration>().injectionCount,
                                geneTransferOperator = get()
                            )
                                .let(wrapIntoLogger),
                            OrderPopulationByCost<Task>(
                                calculateCostOf = get()
                            )
                                .let(wrapIntoLogger),
                            BoostOnBestAndLucky(
                                luckyCount = get<Configuration>().boostLuckyCount,
                                boostOperator = get()
                            )
                                .let(wrapIntoLogger)
                        )
                    )
                }

                single<MutationOnSpecimen> {
                    MutationWithElitistSelection(
                        mutationOperator = get(),
                        calculateCostOf = get(),
                        selectSegments = get(),
                        cloneCount = get<Configuration>().cloneCount,
                        cloneCycleCount = get<Configuration>().cloneCycleCount,
                    )
                }

                single<BacterialMutationOperator> {
                    EdgeBuilderHeuristicOnContinuousSegment(
                        costGraph = get<Task>(),
                        extractEdgeWeight = Int::toFloat
                    )
                }

                single<SelectSegments> {
                    SelectCuts(cloneSegmentLength = get<Configuration>().cloneSegmentLength)
                }

                single<GeneTransferOperator<*>> {
                    GeneTransferByCrossOver<Task>(
                        calculateCostOf = get(),
                        geneTransferSegmentLength = get<Configuration>().geneTransferSegmentLength,
                        crossOverOperator = get()
                    )
                }

                single<CrossOverOperator> {
                    HeuristicCrossOver(
                        costGraph = get(),
                        extractEdgeCost = Int::toFloat
                    )
                }

                single<BoostOperator<*>> {
                    SimplifiedTwoOptStepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
                        get(),
                        stepLimit = get<Configuration>().boostStepLimit,
                        populationSize = get<Configuration>().sizeOfPopulation,
                        permutationSize = get<Configuration>().sizeOfPermutation
                    )
                }

                single<LoggingChannel<*>>(named("state")) {
                    JsonChannel<EvolutionaryAlgorithmStateForLogging>(
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

                single<LoggingChannel<*>>(named("task")) {
                    JsonChannel<MutableCompleteGraph<Unit, Int>>(
                        outputFolder = get<Configuration>().outputFolder,
                        outputFileName = "task",
                        type = LogType.INFO,
                        name = "taskLogger",
                        version = 1,
                    )
                }

                single<LoggingChannel<*>>(named("config")) {
                    JsonChannel<Configuration>(
                        outputFolder = get<Configuration>().outputFolder,
                        outputFileName = "config",
                        type = LogType.INFO,
                        name = "configLogger",
                        version = 1,
                    )
                }
            }
        )
    }

    val algorithmState = get<InitializeEvolutionaryAlgorithm<Task>>()(get())
    val iteration = get<EvolutionaryAlgorithmIteration<EvolutionaryAlgorithmState<Task>>>()
    get<LoggingChannel<Configuration>>("config")
        .apply { initialize() }
        .send(get<Configuration>())
    get<LoggingChannel<MutableCompleteGraph<Unit, Int>>>("task")
        .apply { initialize() }
        .send(get<Task>().toMutable())

    repeat(get<Configuration>().iterationLimit) {
        iteration(algorithmState)
        if (it % 100 == 0) print(".")
    }
    println()

    println(
        algorithmState.population.activesAsSequence()
            .minBy { it.costOrException()[0] }
            .costOrException()
    )
    println(algorithmState.copyOfBest?.costOrException())

    stopKoin()
}