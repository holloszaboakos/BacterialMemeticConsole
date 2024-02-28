package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames
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
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.utility.extention.KoinUtil
import hu.raven.puppet.utility.extention.KoinUtil.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime

//70 successful transcriptions only!
//loose matching and random completion
//iterationOfCreation=8536, cost=FloatVector(coordinates=[17657.0]), 159060 successful transcription
//loose matching and heuristic completion
//iterationOfCreation=3401, cost=FloatVector(coordinates=[17840.0]), 159060 successful transcription
//loose matching and heuristic completion, segment size 4, population 64x4, bacterial mutation 8 cycle 8 clone, 32 transcription per bacteriophage
//iterationOfCreation=2900, cost=FloatVector(coordinates=[16290.0])
//iterationOfCreation=4102, cost=FloatVector(coordinates=[16018.0])
//iterationOfCreation=6844, cost=FloatVector(coordinates=[15583.0])
//iterationOfCreation=9255, cost=FloatVector(coordinates=[15404.0])
//loose matching and heuristic completion, segment size 4, population 64x4, bacterial mutation 16 cycle 4 clone, 32 transcription per bacteriophage
//iterationOfCreation=7838, cost=FloatVector(coordinates=[15535.0])
//Added boost with bacteriophage
//id=195, iterationOfCreation=910, cost=FloatVector(coordinates=[16676.0])

private typealias Task = CompleteGraph<Unit, Int>

fun main() {
    startKoin {
        modules(
            module {
                single(named(FilePathVariableNames.SINGLE_FILE)) { "size64instance0.json" }
                single(named(FilePathVariableNames.INPUT_FOLDER)) { "\\input\\tsp" }
                single(named(FilePathVariableNames.OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
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
                        sizeOfPopulation = 64 * 4,
                        sizeOfTask = 63
                    )
                }
                single<InitializeBacteriophagePopulation> {
                    BasicInitializationOfBacteriophagePopulation(64)
                }
                single {
                    OrderPopulationByCost<Task>(calculateCostOf = get())
                }
                single<CalculateCost<*>> {
                    CalculateCostOfTspSolution(task = get<Task>())
                }
                single<TaskLoaderService<Task>> {
                    TspTaskLoaderService(
                        log = { println(it) },
                        fileName = get(named(FilePathVariableNames.SINGLE_FILE))
                    )
                }
                single {
                    get<TaskLoaderService<Task>>().loadTask(folderPath = get(named(FilePathVariableNames.INPUT_FOLDER)))
                }
                single<AlgorithmIteration<*>> {
                    EvolutionaryAlgorithmIteration<BacteriophageAlgorithmState<Task>>(
                        steps = arrayOf<EvolutionaryAlgorithmStep<BacteriophageAlgorithmState<Task>>>(
                            get<SelectSurvivors>().let { StepLogger(it, get()) },
                            get<BacterialMutation>().let { StepLogger(it, get()) },
                            get<BacteriophageTranscription<Task>>().let { StepLogger(it, get()) },
                            get<OrderPopulationByCost<Task>>().let { StepLogger(it, get()) },
                            get<BoostStrategy>().let { StepLogger(it, get()) },
                        )
                    )
                }
                single<SelectSurvivors> {
                    SelectSurvivorsMultiObjectiveHalfElitist
                }
                single<BacterialMutation> {
                    BacterialMutationOnBestAndLuckyByShuffling(get(), 1f)
                }
                single<MutationOnSpecimen> {
                    MutationOnSpecimenWithBacteriophageTransduction(
                        MutationWithElitistSelection(
                            get(),
                            get(),
                            get(),
                            cloneCount = 2,
                            cloneCycleCount = 32
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
                single<BacterialMutationOperator> { EdgeBuilderHeuristicOnContinuousSegment(get()) }
                single<SelectSegments> { SelectCuts(4) }
                single<BacteriophageTranscription<Task>> {
                    BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion<Task>(
                        1 / 8f,
                        0.5f,
                        0.5f,
                        get(),
                        get()
                    )
                }
                single<BoostStrategy> {
                    BoostOnBestAndLucky(luckyCount = 64, get())
                }
                single<BoostOperator<*>> {
                    BoostOperatorWithBacteriophageTransduction(
                        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
                            get(),
                            stepLimit = 64,
                            populationSize = 64 * 4,
                            permutationSize = 63
                        ),
                        get(),
                        KoinUtil::get
                    )
                }
                single<LoggingChannel<*>> {
                    JsonChannel<BacteriophageAlgorithmState<Task>>(
                        outputFolder = arrayOf(
                            "output", "${LocalDate.now()}",
                            LocalDateTime.now().toString()
                                .replace(":", "_")
                                .replace(".", "_")
                        ),
                        outputFileName = "algorithmState"
                    )
                }
            }
        )
    }

    val iteration: AlgorithmIteration<BacteriophageAlgorithmState<Task>> = get()
    val algorithmState: BacteriophageAlgorithmState<Task> = get()

    repeat(10_000) {
        iteration(algorithmState)
        println("iteration $it.: ${algorithmState.copyOfBest}")
    }

    stopKoin()
}