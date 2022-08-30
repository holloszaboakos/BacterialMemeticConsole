package hu.raven.puppet

import hu.raven.puppet.logic.common.initialize.InitializeAlgorithm
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.common.iteration.EvolutionaryIteration
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.modules.commonModule
import hu.raven.puppet.logic.statistics.Statistics
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(arguments: Array<String>) {
    startKoin {
        modules(commonModule)
    }
    val statistics: Statistics<*> by inject(Statistics::class.java)
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    val initialize: InitializeAlgorithm<*> by inject(InitializeAlgorithm::class.java)
    val iterate: EvolutionaryIteration<*> by inject(EvolutionaryIteration::class.java)

    val argumentMap = loadArgumentsToMap(arguments)

    val outputFolderPath = argumentMap["-outputFolderPath"] ?: throw Error("No path given")
    val time = LocalDateTime.now().toString().split('.')[0].replace(':', '-')
    val outputFile = File("$outputFolderPath\\statistics-$time.txt")
    logger.targetFile = outputFile

    val localSearch: SLocalSearch<*> by inject(SLocalSearch::class.java)
    localSearch.task = loadTask(argumentMap)
    initialize()

    var index = 0
    while (true) {
        logger("STEP: ${index + 1}")
        val duration = measureTime {
            iterate(false)
        }
        logger("time elapsed: $duration")

        val best = localSearch.actualInstance
        logger.logSpecimen("best", best)

        logger("fitness cost call count: ${statistics.fitnessCallCount}")

        println()

        if (statistics.fitnessCallCount > 5000000) {
            break
        }
        index++
    }
}