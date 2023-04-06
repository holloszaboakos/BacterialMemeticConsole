package hu.raven.puppet.runScenario

/*
@ExperimentalTime
fun main() {
    startKoin {
        modules(
            defaultDataSetModule,
            commonModule,
            bacterialModule
        )
    }

    val iterationLimit: Int by inject(AlgorithmParameters.ITERATION_LIMIT)
    val initialize: InitializeAlgorithm<*, *> by inject()
    val iterate: EvolutionaryIteration<*, *> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()
    val doubleLogger: DoubleLogger by inject()
    val csvLogger: CSVLogger by inject()

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

            if (statistics.fitnessCallCount > 5_000_000) {
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
    val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<OnePartRepresentation<C>, C> by inject()

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

private fun <C : PhysicsUnit<C>> OnePartRepresentation<C>.toLog() = SpecimenData(id, cost!!)


 */

