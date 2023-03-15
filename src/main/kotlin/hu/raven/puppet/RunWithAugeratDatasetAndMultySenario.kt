package hu.raven.puppet

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationWithElitistSelectionAndOneOposition
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.bacterialmutationoperator.EdgeBuilderHeuristicOnContinuousSegment
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.boost.BoostOnBest
import hu.raven.puppet.logic.step.boost.BoostOnBestLazy
import hu.raven.puppet.logic.step.boost.NoBoost
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.step.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.step.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.step.diversity.Diversity
import hu.raven.puppet.logic.step.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.step.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferByCrossOver
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.step.iterationofevolutionary.BacterialIteration
import hu.raven.puppet.logic.step.iterationofevolutionary.EvolutionaryIteration
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.logic.task.loader.AugeratTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.model.logging.BacterialMemeticAlgorithmLogLine
import hu.raven.puppet.model.logging.PopulationData
import hu.raven.puppet.model.logging.ProgressData
import hu.raven.puppet.model.logging.SpecimenData
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.OnePartRepresentationFactory
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.extention.logProgress
import hu.raven.puppet.utility.extention.logSpecimen
import hu.raven.puppet.utility.extention.logStepEfficiency
import hu.raven.puppet.utility.extention.median
import hu.raven.puppet.utility.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val taskCommonModule = module {
    single(named(FilePathVariableNames.INPUT_FOLDER)) {
        "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\input"
    }
    single<TaskLoader> { AugeratTaskLoader() }
    factory<CalculateCost<*, *>> {
        CalculateCostOfCVRPSolutionWithCapacity<OnePartRepresentation<Meter>>()
    }
}

private val taskModules = arrayOf(
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 5 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n32-k05.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 5 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n34-k05.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 6 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n39-k06.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 7 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n45-k07.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 7 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n48-k07.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 9 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n55-k09.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 9 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n60-k09.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 9 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n69-k09.xml" }
    },
    module {
        single(named(AlgorithmParameters.VEHICLE_COUNT)) { 10 }
        single(named(FilePathVariableNames.SINGLE_FILE)) { "augerat-1995-set-a\\A-n80-k10.xml" }
    },
)

private val bacterialCommonModule = module {
    single<IterativeAlgorithmStateWithMultipleCandidates<OnePartRepresentation<Meter>, Meter>> {
        IterativeAlgorithmStateWithMultipleCandidates()
    }

    factory<InitializeAlgorithm<*, *>> {
        InitializeBacterialAlgorithm<OnePartRepresentation<Meter>, Meter>()
    }

    factory<EvolutionaryIteration<*, *>> {
        BacterialIteration<OnePartRepresentation<Meter>, Meter>()
    }

    factory<BacterialMutation<*, *>> {
        BacterialMutationOnBestAndLuckyByShuffling<OnePartRepresentation<Meter>, Meter>()
    }
    factory<MutationOnSpecimen<*, *>> {
        MutationWithElitistSelectionAndOneOposition<OnePartRepresentation<Meter>, Meter>()
    }
    factory<BacterialMutationOperator<*, *>> {
        EdgeBuilderHeuristicOnContinuousSegment<OnePartRepresentation<Meter>, Meter>()
    }
    factory<hu.raven.puppet.logic.step.genetransfer.GeneTransfer<*, *>> {
        GeneTransferByTournament<OnePartRepresentation<Meter>, Meter>()
    }
    factory<GeneTransferOperator<*, *>> {
        GeneTransferByCrossOver<OnePartRepresentation<Meter>, Meter>()
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<OnePartRepresentation<Meter>, Meter>()
    }
    factory<SelectSegment<*, *>> {
        SelectContinuesSegment<OnePartRepresentation<Meter>, Meter>()
    }

    single {
        BacterialAlgorithmStatistics()
    }

    single {
        CSVLogger()
    }
}

private val bacterialModules = arrayOf(
    module {
        single(named(AlgorithmParameters.ITERATION_LIMIT)) { Int.MAX_VALUE }
        single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 1 }

        single(named(AlgorithmParameters.MUTATION_PERCENTAGE)) { 1f }
        single(named(AlgorithmParameters.CLONE_COUNT)) { 5 }
        single(named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)) { 8 }
        single(named(AlgorithmParameters.CLONE_CYCLE_COUNT)) { 5 }

        single(named(AlgorithmParameters.GENE_TRANSFER_SEGMENT_LENGTH)) { 0 }
        single(named(AlgorithmParameters.INJECTION_COUNT)) { 0 }
        single(named(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)) { 0 }
    },
    module {
        single(named(AlgorithmParameters.ITERATION_LIMIT)) { Int.MAX_VALUE }
        single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 1 }

        single(named(AlgorithmParameters.MUTATION_PERCENTAGE)) { 1f }
        single(named(AlgorithmParameters.CLONE_COUNT)) { 5 }
        single(named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)) { 8 }
        single(named(AlgorithmParameters.CLONE_CYCLE_COUNT)) { 5 }

        single(named(AlgorithmParameters.GENE_TRANSFER_SEGMENT_LENGTH)) { 0 }
        single(named(AlgorithmParameters.INJECTION_COUNT)) { 0 }

        single(named(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)) { 100 }
    },
    module {
        single(named(AlgorithmParameters.ITERATION_LIMIT)) { Int.MAX_VALUE }
        single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 4 }

        single(named(AlgorithmParameters.MUTATION_PERCENTAGE)) { 1f }
        single(named(AlgorithmParameters.CLONE_COUNT)) { 5 }
        single(named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)) { 8 }
        single(named(AlgorithmParameters.CLONE_CYCLE_COUNT)) { 5 }

        single(named(AlgorithmParameters.GENE_TRANSFER_SEGMENT_LENGTH)) { 0 }
        single(named(AlgorithmParameters.INJECTION_COUNT)) { 2 }

        single(named(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)) { 100 }
    },
    module {
        single(named(AlgorithmParameters.ITERATION_LIMIT)) { Int.MAX_VALUE }
        single(named(AlgorithmParameters.SIZE_OF_POPULATION)) { 10 }

        single(named(AlgorithmParameters.MUTATION_PERCENTAGE)) { 1f }
        single(named(AlgorithmParameters.CLONE_COUNT)) { 5 }
        single(named(AlgorithmParameters.CLONE_SEGMENT_LENGTH)) { 8 }
        single(named(AlgorithmParameters.CLONE_CYCLE_COUNT)) { 5 }

        single(named(AlgorithmParameters.GENE_TRANSFER_SEGMENT_LENGTH)) { 0 }
        single(named(AlgorithmParameters.INJECTION_COUNT)) { 5 }

        single(named(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)) { 100 }
    },
)

private val commonModule = module {
    single(named(FilePathVariableNames.OUTPUT_FOLDER)) {
        "D:\\Git\\GitHub\\SourceCodes\\Kotlin\\JVM\\BacterialMemeticConsole\\output\\augeratLimited"
    }

    single {
        DoubleLogger()
    }

    single {
        CSVLogger()
    }

    single {
        VRPTaskHolder()
    }

    factory<Diversity<*, *>> {
        DiversityByInnerDistanceAndSequence<OnePartRepresentation<Meter>, Meter>()
    }

    factory<SolutionRepresentationFactory<*, *>> {
        OnePartRepresentationFactory()
    }

    factory<InitializePopulation<*, *>> {
        InitializePopulationByModuloStepper<OnePartRepresentation<Meter>, Meter>()
    }

    factory {
        OrderPopulationByCost<OnePartRepresentation<Meter>, Meter>()
    }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

}

private val boostModules = arrayOf(
    module {
        factory<Boost<*, *>> {
            NoBoost<OnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<OnePartRepresentation<Meter>, Meter>()
        }
    },
    module {
        factory<Boost<*, *>> {
            BoostOnBest<OnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<OnePartRepresentation<Meter>, Meter>()
        }
    },
    module {
        factory<Boost<*, *>> {
            BoostOnBest<OnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<OnePartRepresentation<Meter>, Meter>()
        }
    },
    module {
        factory<Boost<*, *>> {
            BoostOnBestLazy<OnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<OnePartRepresentation<Meter>, Meter>()
        }
    },
)

@ExperimentalTime
fun main() {
    taskModules.indices.forEach { dataSetIndex ->
        bacterialModules.indices.forEach { scenarioIndex ->
            repeat(10) { roundIndex ->
                startKoin {
                    modules(
                        taskCommonModule,
                        taskModules[dataSetIndex],
                        bacterialCommonModule,
                        bacterialModules[scenarioIndex],
                        commonModule,
                        boostModules[scenarioIndex]
                    )
                }
                runAlgorithm(
                    dataSetIndex,
                    scenarioIndex,
                    roundIndex
                )
                stopKoin()
            }
        }
    }
}

@ExperimentalTime
private fun runAlgorithm(
    dataSetIndex: Int,
    scenarioIndex: Int,
    roundIndex: Int
) {

    val iterationLimit: Int by inject(AlgorithmParameters.ITERATION_LIMIT)
    val initialize: InitializeAlgorithm<*, *> by inject()
    val iterate: EvolutionaryIteration<*, *> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()
    val doubleLogger: DoubleLogger by inject()
    val csvLogger: CSVLogger by inject()

    val outputFileName =
        "statistics-scenario$scenarioIndex-dataset$dataSetIndex-round$roundIndex-${csvLogger.creationTime}"

    doubleLogger.targetFileName = outputFileName
    csvLogger.targetFileName = outputFileName

    csvLogger.printHeader()

    val fullRuntime = measureTime {
        initialize()
        var timeElapsedTotal = Duration.ZERO
        var fitnessCallCountOld = 0L
        for (index in 0 until iterationLimit) {
            val timeElapsed = measureTime {
                iterate()
            }

            timeElapsedTotal += timeElapsed


            val logData = calculateLogData(
                statistics,
                fitnessCallCountOld,
                timeElapsed,
                timeElapsedTotal
            )

            logGeneration(logData)
            csvLogger(logData)

            fitnessCallCountOld = statistics.fitnessCallCount

            println()

            if (statistics.fitnessCallCount > 50_000) {
                break
            }
        }

    }

    doubleLogger("FULL RUNTIME: $fullRuntime")
}

private fun <C : PhysicsUnit<C>> logGeneration(logData: BacterialMemeticAlgorithmLogLine<C>) {
    val logger: DoubleLogger by inject()

    logger.logProgress(logData.progressData)

    logData.populationData.apply {
        logger.logSpecimen("best", best)
        second?.also {
            logger.logSpecimen("second", it)
        }
        third?.also {
            logger.logSpecimen("third", it)
        }
        logger.logSpecimen("worst", worst)
    }

    logData.apply {
        logger.logStepEfficiency("mutation", mutationImprovement)
        logger.logStepEfficiency("mutation on best", mutationOnBestImprovement)
        logger.logStepEfficiency("gene transfer", geneTransferImprovement)
        logger.logStepEfficiency("boost", boostImprovement)
        logger.logStepEfficiency("boost on best", boostOnBestImprovement)
    }
}

private fun <C : PhysicsUnit<C>> calculateLogData(
    statistics: BacterialAlgorithmStatistics,
    fitnessCallCountOld: Long,
    timeElapsed: Duration,
    timeElapsedTotal: Duration
): BacterialMemeticAlgorithmLogLine<C> {
    val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<SolutionRepresentation<C>, C> by inject()

    val best = algorithmState.copyOfBest!!
    val second =
        if (algorithmState.population.size > 1)
            algorithmState.population[1]
        else null
    val third =
        if (algorithmState.population.size > 2)
            algorithmState.population[2]
        else null
    val worst = algorithmState.copyOfWorst!!
    val median = algorithmState.population.median()

    val logOfBest = best.toLog()
    val logOfSecond = second?.toLog()
    val logOfThird = third?.toLog()
    val logOfWorst = worst.toLog()
    val logOfMedian = median.toLog()

    return BacterialMemeticAlgorithmLogLine<C>(
        ProgressData(
            generation = algorithmState.iteration + 1,
            spentTimeTotal = timeElapsedTotal,
            spentTimeOfGeneration = timeElapsed,
            spentBudgetTotal = statistics.fitnessCallCount,
            spentBudgetOfGeneration = statistics.fitnessCallCount - fitnessCallCountOld
        ),
        PopulationData<C>(
            best = logOfBest,
            second = logOfSecond,
            third = logOfThird,
            worst = logOfWorst,
            median = logOfMedian,
            diversity = statistics.diversity, //TODO
            geneBalance = 0.0 //TODO
        ),
        mutationImprovement = statistics.mutationImprovement,
        mutationOnBestImprovement = statistics.mutationOnBestImprovement,
        geneTransferImprovement = statistics.geneTransferImprovement,
        boostImprovement = statistics.boostImprovement,
        boostOnBestImprovement = statistics.boostOnBestImprovement
    )
}

private fun <C : PhysicsUnit<C>> SolutionRepresentation<C>.toLog() = SpecimenData(id, cost!!)
