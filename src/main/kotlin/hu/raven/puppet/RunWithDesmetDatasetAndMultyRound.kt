package hu.raven.puppet

import hu.raven.puppet.logic.logging.CSVLogger
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.step.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.model.logging.BacterialMemeticAlgorithmLogLine
import hu.raven.puppet.model.logging.PopulationData
import hu.raven.puppet.model.logging.ProgressData
import hu.raven.puppet.model.logging.SpecimenData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.modules.bacterialModule
import hu.raven.puppet.modules.dataset.desmetDataSetModule
import hu.raven.puppet.utility.extention.logProgress
import hu.raven.puppet.utility.extention.logSpecimen
import hu.raven.puppet.utility.extention.logStepEfficiency
import hu.raven.puppet.utility.extention.median
import hu.raven.puppet.utility.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun main() {
    repeat(10) { roundIndex ->
        startKoin {
            modules(
                desmetDataSetModule,
                hu.raven.puppet.modules.commonModule,
                bacterialModule
            )
        }
        runAlgorithm(roundIndex)
        stopKoin()
    }
}

@ExperimentalTime
private fun runAlgorithm(
    roundIndex: Int
) {


    val iterationLimit: Int by inject(AlgorithmParameters.ITERATION_LIMIT)
    val initialize: InitializeAlgorithm<*, *> by inject()
    val iterate: EvolutionaryIteration<*, *> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()
    val doubleLogger: DoubleLogger by inject()
    val csvLogger: CSVLogger by inject()

    val outputFileName = "statistics-p3-round$roundIndex-${csvLogger.creationTime}"

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

            if (statistics.fitnessCallCount > 1_000_000) {
                break
            }
        }

    }

    doubleLogger("FULL RUNTIME: $fullRuntime")
}

private fun logGeneration(logData: BacterialMemeticAlgorithmLogLine<*>) {
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
    val second: ISpecimenRepresentation<C>? =
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

private fun <C : PhysicsUnit<C>> ISpecimenRepresentation<C>.toLog() = SpecimenData(id, cost!!)
