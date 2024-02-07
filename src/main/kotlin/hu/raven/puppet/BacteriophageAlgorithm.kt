package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeBacteriophageAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimenWithBacteriophageTransduction
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationWithElitistSelection
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.EdgeBuilderHeuristicOnContinuousSegment
import hu.raven.puppet.logic.operator.bacteriophage_transduction.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.BasicInitializationOfBacteriophagePopulation
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.InitializeBacteriophagePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.operator.select_segments.SelectCuts
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.logic.operator.select_segments.SelectSingleValuesContinuouslyWithFullCoverage
import hu.raven.puppet.logic.step.bacterial_mutation.BacterialMutation
import hu.raven.puppet.logic.step.bacterial_mutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.bacteriophage_transcription.BacteriophageTranscription
import hu.raven.puppet.logic.step.bacteriophage_transcription.BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivors
import hu.raven.puppet.logic.step.select_survivers.SelectSurvivorsMultiObjectiveHalfElitist
import hu.raven.puppet.logic.task.loader.TaskLoaderService
import hu.raven.puppet.logic.task.loader.TspTaskLoaderService
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.utility.extention.KoinUtil
import hu.raven.puppet.utility.extention.KoinUtil.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path

//70 successful transcriptions only!
//loose matching and random completion
//iterationOfCreation=8536, cost=FloatVector(coordinates=[17657.0]), 159060 successful transcription
//loose matching and heuristic completion
//iterationOfCreation=3401, cost=FloatVector(coordinates=[17840.0]), 159060 successful transcription
//loose matching and heuristic completion, population 64x64, bacterial mutation 8 cycle 8 clone, 8 transcription per bacteriophage
fun main() {
    startKoin {
        modules(
            module {
                single(named(FilePathVariableNames.SINGLE_FILE)) { "size64instance0.json" }
                single(named(FilePathVariableNames.INPUT_FOLDER)) { "\\input\\tsp" }
                single(named(FilePathVariableNames.OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
                single<InitializeAlgorithm<*>> {
                    InitializeBacteriophageAlgorithm(
                        InitializeEvolutionaryAlgorithm(
                            initializePopulation = get(),
                            orderPopulationByCost = get()
                        ),
                        get()
                    )
                }
                single<InitializePopulation> {
                    InitializePopulationByModuloStepper(64 * 8)
                }
                single<InitializeBacteriophagePopulation> {
                    BasicInitializationOfBacteriophagePopulation(64)
                }
                single {
                    OrderPopulationByCost(calculateCostOf = get())
                }
                single<CalculateCost> {
                    CalculateCostOfTspSolution(task = get())
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
                single {
                    get<TaskLoaderService>().loadTask(folderPath = get(named(FilePathVariableNames.INPUT_FOLDER)))
                }
                single<AlgorithmIteration<*>> {
                    EvolutionaryAlgorithmIteration(
                        steps = arrayOf(
                            get<SelectSurvivors>(),
                            get<BacterialMutation>(),
                            get<BacteriophageTranscription>(),
                            get<OrderPopulationByCost>(),
                            //get<BoostStrategy>(),
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
                            cloneCount = 8,
                            cloneCycleCount = 8
                        ),
                        get(),
                        KoinUtil::get
                    )
                }
                single<BacteriophageAlgorithmState> { get<InitializeAlgorithm<BacteriophageAlgorithmState>>()(get()) }
                single { BacteriophageTransductionOperator() }
                single<BacterialMutationOperator> { EdgeBuilderHeuristicOnContinuousSegment(get()) }
                single<SelectSegments> { SelectCuts(8) }
                single<BacteriophageTranscription> {
                    BacteriophageTranscriptionByLooseMatchingAndHeuristicCompletion(
                        1 / 64f,
                        0.5f,
                        0.5f,
                        get(),
                        get()
                    )
                }
            }
        )
    }

    val iteration: AlgorithmIteration<BacteriophageAlgorithmState> = get()
    val algorithmState: BacteriophageAlgorithmState = get()

    repeat(10_000) {
        iteration(algorithmState)
        println("iteration $it.: ${algorithmState.copyOfBest}")
    }

    stopKoin()
}