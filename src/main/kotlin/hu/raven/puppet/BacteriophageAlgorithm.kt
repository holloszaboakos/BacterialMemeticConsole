package hu.raven.puppet

import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.initialize.InitializeBacteriophageAlgorithm
import hu.raven.puppet.logic.initialize.InitializeEvolutionaryAlgorithm
import hu.raven.puppet.logic.iteration.AlgorithmIteration
import hu.raven.puppet.logic.iteration.EvolutionaryAlgorithmIteration
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationOnSpecimenWithBacteriophageTransduction
import hu.raven.puppet.logic.operator.bacterialmutationonspecimen.MutationWithElitistSelection
import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacterialmutationoperator.EdgeBuilderHeuristicOnContinuousSegment
import hu.raven.puppet.logic.operator.bacteriophagetransduction.BacteriophageTransductionOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.calculatecost.CalculateCostOfTspSolution
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.BasicInitializationOfBacteriophagePopulation
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.InitializeBacteriophagePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.logic.operator.selectsegments.SelectSingleValuesContinuouslyWithFullCoverage
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.bacteriophagetranscription.BacteriophageTranscription
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivorsMultiObjectiveHalfElitist
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
                    InitializePopulationByModuloStepper(64 )
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
                            cloneCount = 4,
                            cloneCycleCount = 16
                        ),
                        get(),
                        KoinUtil::get
                    )
                }
                single<BacteriophageAlgorithmState> { get<InitializeAlgorithm<BacteriophageAlgorithmState>>()(get()) }
                single { BacteriophageTransductionOperator() }
                single<BacterialMutationOperator> { EdgeBuilderHeuristicOnContinuousSegment(get()) }
                single<SelectSegments> { SelectSingleValuesContinuouslyWithFullCoverage(8) }
                single { BacteriophageTranscription(1 / 8f, 0.5f, 0.5f, get()) }
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