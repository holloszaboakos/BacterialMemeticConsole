package hu.raven.puppet.job

import hu.akos.hollo.szabo.math.Permutation
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
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.state.serializable.EvolutionaryAlgorithmStateForLogging
import hu.raven.puppet.model.task.TspTask
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.MutableCompleteGraph
import hu.raven.puppet.model.utility.math.toMutable
import hu.raven.puppet.utility.extention.KoinUtil
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.LocalDate
import java.time.LocalDateTime

typealias Task = CompleteGraph<Unit, Int>

fun main() {
    runBacterial(
        Configuration(
            fileName = "instance8.json",
            inputFolder = "D:\\Research\\Datasets\\tsp64x10_000",
            outputFolder = listOf(
                "D:", "Research", "Results2", "${LocalDate.now()}",
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
                    InitializeEvolutionaryAlgorithm<TspTask, Permutation>(
                        initializePopulation = get(),
                        orderPopulationByCost = get(),
                    )
                }

                single<InitializePopulation<*>> {
                    InitializePopulationByModuloStepper(
                        sizeOfPopulation = get<Configuration>().sizeOfPopulation,
                        sizeOfTask = get<Configuration>().sizeOfPermutation
                    )
                }

                single {
                    OrderPopulationByCost<Permutation, TspTask>(
                        calculateCostOf = get()
                    )
                }

                single<CalculateCost<*, *>> {
                    CalculateCostWithLogging(
                        classOfSolutionRepresentation = Permutation::class.java,
                        calculateCost = CalculateCostOfTspSolution(task = get<TspTask>()),
                        loggingChannel = get(named("cost")),
                        task = get<TspTask>()
                    )
                }

                single<TspTask> {
                    TspFromMatrixTaskLoaderService(
                        fileName = get<Configuration>().fileName,
                        log = { println(it) }
                    )
                        .loadTask(get<Configuration>().inputFolder)
                }

                single {
                    val stateToSerializableMapper = { state: EvolutionaryAlgorithmState<Permutation> ->
                        EvolutionaryAlgorithmStateForLogging(
                            population = state.population.activesAsSequence().toList(),
                            iteration = state.iteration
                        )
                    }

                    val wrapIntoLogger =
                        { state: EvolutionaryAlgorithmStep<Permutation, EvolutionaryAlgorithmState<Permutation>> ->
                            StepLogger(
                                state,
                                get(named("state")),
                                stateToSerializableMapper
                            )
                        }

                    EvolutionaryAlgorithmIteration(
                        steps = arrayOf(
                            BacterialMutationOnBestAndLuckyByShuffling<Permutation>(
                                mutationOnSpecimen = get(),
                                mutationPercentage = get<Configuration>().mutationPercentage
                            )
                                .let(wrapIntoLogger),
                            GeneTransferByTournament<Permutation>(
                                injectionCount = get<Configuration>().injectionCount,
                                geneTransferOperator = get()
                            )
                                .let(wrapIntoLogger),
                            OrderPopulationByCost<Permutation, TspTask>(
                                calculateCostOf = get()
                            )
                                .let(wrapIntoLogger),
                            BoostOnBestAndLucky<Permutation>(
                                luckyCount = get<Configuration>().boostLuckyCount,
                                boostOperator = get()
                            )
                                .let(wrapIntoLogger)
                        )
                    )
                }

                single<MutationOnSpecimen<*, *>> {
                    MutationWithElitistSelection<SolutionWithIteration<Permutation>>(
                        mutationOperator = get(),
                        calculateCostOf = get(),
                        selectSegments = get(),
                        cloneCount = get<Configuration>().cloneCount,
                        cloneCycleCount = get<Configuration>().cloneCycleCount,
                    )
                }

                single<BacterialMutationOperator<*, *>> {
                    EdgeBuilderHeuristicOnContinuousSegment(
                        costGraph = get<Task>(),
                        extractEdgeWeight = Int::toFloat
                    )
                }

                single<SelectSegments> {
                    SelectCuts(cloneSegmentLength = get<Configuration>().cloneSegmentLength)
                }

                single<GeneTransferOperator<*, *>> {
                    GeneTransferByCrossOver<SolutionWithIteration<Permutation>>(
                        calculateCostOf = get(),
                        geneTransferSegmentLength = get<Configuration>().geneTransferSegmentLength,
                        crossOverOperator = get()
                    )
                }

                single<CrossOverOperator<*>> {
                    HeuristicCrossOver(
                        costGraph = get(),
                        extractEdgeCost = Int::toFloat
                    )
                }

                single<BoostOperator<*, *>> {
                    SimplifiedTwoOptStepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
                        get(),
                        stepLimit = get<Configuration>().boostStepLimit,
                        populationSize = get<Configuration>().sizeOfPopulation,
                        permutationSize = get<Configuration>().sizeOfPermutation
                    )
                }

                single<LoggingChannel<*>>(named("state")) {
                    JsonChannel<EvolutionaryAlgorithmStateForLogging<Permutation>>(
                        outputFolder = get<Configuration>().outputFolder,
                        outputFileName = "algorithmState",
                        type = LogType.INFO,
                        name = "perStepStateLogger",
                        version = 1,
                    )
                }

                single<LoggingChannel<*>>(named("cost")) {
                    JsonChannel<Pair<Permutation, List<Float>>>(
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

    val algorithmState = KoinUtil.get<InitializeEvolutionaryAlgorithm<TspTask, Permutation>>()(
        KoinUtil.get()
    )
    val iteration =
        KoinUtil.get<EvolutionaryAlgorithmIteration<Permutation, EvolutionaryAlgorithmState<Permutation>>>()
    KoinUtil.getBy<LoggingChannel<Configuration>>("config")
        .apply { initialize() }
        .send(KoinUtil.get<Configuration>())
    KoinUtil.getBy<LoggingChannel<MutableCompleteGraph<Unit, Int>>>("task")
        .apply { initialize() }
        .send(KoinUtil.get<Task>().toMutable())

    repeat(KoinUtil.get<Configuration>().iterationLimit) {
        iteration(algorithmState)
        if (it % 100 == 0) print(".")
    }
    println()

    println(
        algorithmState.population.activesAsSequence()
            .minBy { it.value.costOrException()[0] }
            .value.costOrException()
    )
    println(algorithmState.copyOfBest?.value?.costOrException())

    stopKoin()
}