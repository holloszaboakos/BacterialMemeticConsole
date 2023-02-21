package hu.raven.puppet

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.specimen.factory.OnePartRepresentationFactory
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCostOfCVRPSolutionWithCapacity
import hu.raven.puppet.logic.step.common.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.step.common.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.step.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.common.initialize.InitializeBacterialAlgorithm
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer.GeneTransferByTournament
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator.GeneTransferByCrossOver
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutation.BacterialMutationOnBestAndLuckyByShuffling
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationonspecimen.MutationOnSpecimenWithRandomContinuousSegmentAndFullCoverAndCloneWithInvertion
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator.MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectContinuesSegment
import hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.common.boost.BoostOnBest
import hu.raven.puppet.logic.step.evolutionary.common.boost.BoostOnBestLazy
import hu.raven.puppet.logic.step.evolutionary.common.boost.NoBoost
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.step.evolutionary.common.diversity.Diversity
import hu.raven.puppet.logic.step.evolutionary.common.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.step.evolutionary.common.iteration.BacterialIteration
import hu.raven.puppet.logic.step.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.evolutionary.genetic.crossoveroperator.HeuristicCrossOver
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.logic.task.loader.AugeratTaskLoader
import hu.raven.puppet.logic.task.loader.TaskLoader
import hu.raven.puppet.model.logging.BacterialMemeticAlgorithmLogLine
import hu.raven.puppet.model.logging.PopulationData
import hu.raven.puppet.model.logging.ProgressData
import hu.raven.puppet.model.logging.SpecimenData
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.PhysicsUnit
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
        CalculateCostOfCVRPSolutionWithCapacity<DOnePartRepresentation<Meter>>()
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
    single<EvolutionaryAlgorithmState<DOnePartRepresentation<Meter>, Meter>> {
        EvolutionaryAlgorithmState()
    }

    factory<InitializeAlgorithm<*, *>> {
        InitializeBacterialAlgorithm<DOnePartRepresentation<Meter>, Meter>()
    }

    factory<EvolutionaryIteration<*, *>> {
        BacterialIteration<DOnePartRepresentation<Meter>, Meter>()
    }

    factory<BacterialMutation<*, *>> {
        BacterialMutationOnBestAndLuckyByShuffling<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<MutationOnSpecimen<*, *>> {
        MutationOnSpecimenWithRandomContinuousSegmentAndFullCoverAndCloneWithInvertion<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<BacterialMutationOperator<*, *>> {
        MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<GeneTransfer<*, *>> {
        GeneTransferByTournament<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<GeneTransferOperator<*, *>> {
        GeneTransferByCrossOver<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<CrossOverOperator<*, *>> {
        HeuristicCrossOver<DOnePartRepresentation<Meter>, Meter>()
    }
    factory<SelectSegment<*, *>> {
        SelectContinuesSegment<DOnePartRepresentation<Meter>, Meter>()
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
        DiversityByInnerDistanceAndSequence<DOnePartRepresentation<Meter>, Meter>()
    }

    factory<SSpecimenRepresentationFactory<*, *>> {
        OnePartRepresentationFactory()
    }

    factory<InitializePopulation<*, *>> {
        InitializePopulationByModuloStepper<DOnePartRepresentation<Meter>, Meter>()
    }

    factory {
        OrderPopulationByCost<DOnePartRepresentation<Meter>, Meter>()
    }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

}

private val boostModules = arrayOf(
    module {
        factory<Boost<*, *>> {
            NoBoost<DOnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation<Meter>, Meter>()
        }
    },
    module {
        factory<Boost<*, *>> {
            BoostOnBest<DOnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation<Meter>, Meter>()
        }
    },
    module {
        factory<Boost<*, *>> {
            BoostOnBest<DOnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation<Meter>, Meter>()
        }
    },
    module {
        factory<Boost<*, *>> {
            BoostOnBestLazy<DOnePartRepresentation<Meter>, Meter>()
        }
        factory<BoostOperator<*, *>> {
            Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation<Meter>, Meter>()
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
    val algorithmState: EvolutionaryAlgorithmState<ISpecimenRepresentation<C>, C> by inject()

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

private fun <C : PhysicsUnit<C>> ISpecimenRepresentation<C>.toLog() = SpecimenData<C>(id, cost!!)
