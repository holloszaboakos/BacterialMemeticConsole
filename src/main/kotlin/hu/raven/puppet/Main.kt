package hu.raven.puppet

import com.google.gson.Gson
import hu.raven.puppet.logic.AlgorithmManagerService
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.common.diversity.Diversity
import hu.raven.puppet.logic.logicModule
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import hu.raven.puppet.model.mtsp.*
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


@ExperimentalTime
fun main(arguments: Array<String>) {
    startKoin {
        modules(logicModule)
    }
    val statistics: Statistics by inject(Statistics::class.java)
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    val diversity: Diversity by inject(Diversity::class.java)

    val argumentMap = loadArgumentsToMap(arguments)

    val outputFolderPath = argumentMap["-outputFolderPath"] ?: throw Error("No path given")
    val time = LocalDateTime.now().toString().split('.')[0].replace(':', '-')
    val outputFile = File("$outputFolderPath\\statistics-$time.txt")
    logger.targetFile = outputFile

    val fullRuntime = measureTime {
        val algorithmManager: AlgorithmManagerService by inject(AlgorithmManagerService::class.java)
        algorithmManager.apply {
            task = loadTask(argumentMap)
            checkIfTaskIsWellFormatted()
            initializeAlgorithm()
        }

        for (index in 0 until algorithmManager.algorithm.iterationLimit) {
            logger("STEP: ${index + 1}")
            val duration = measureTime {
                algorithmManager.runIteration()
            }
            logger("time elapsed: $duration")

            val best = algorithmManager.algorithm.copyOfBest!!
            val second = algorithmManager.algorithm.population[1]
            val third = algorithmManager.algorithm.population[2]
            val worst = algorithmManager.algorithm.copyOfWorst!!
            logger.logSpecimen("best", best)
            logger.logSpecimen("second", second)
            logger.logSpecimen("third", third)
            logger.logSpecimen("worst", worst)

            //diversity(algorithmManager.algorithm)
            logger("diversity: ${statistics.diversity}")
            logger("fitness cost call count: ${statistics.fitnessCallCount}")
            logger("mutation operator call count: ${statistics.mutationOperatorCall}")
            logger("mutation improvement count on best: ${statistics.mutationImprovementCountOnBest}")
            logger("mutation improvement count on all: ${statistics.mutationImprovementCountOnAll}")

            println()

            if (statistics.fitnessCallCount > 5_000_000) {
                break
            }
        }
    }

    println("FULL RUNTIME: $fullRuntime")

}

fun getArgumentOrError(argumentMap: Map<String, String>, argumentName: String): String {
    return argumentMap["-$argumentName"] ?: throw Error("No path given")
}

fun loadFileFromArguments(argumentMap: Map<String, String>, argumentName: String): File {
    val filePath = getArgumentOrError(argumentMap, argumentName)
    return File(filePath)
}

inline fun <reified T : Any> loadFromFileGivenInArguments(
    argumentMap: Map<String, String>,
    argumentName: String
): T {
    val file = loadFileFromArguments(argumentMap, argumentName)
    val gson = Gson()
    return gson.fromJson(file.readText(), T::class.java)
}

fun loadTask(argumentMap: Map<String, String>): DTask {
    val incompleteGraph: DGraph = loadFromFileGivenInArguments(argumentMap, "graphFilePath")
    val edgesBetween: Array<DEdgeArray> = loadFromFileGivenInArguments(argumentMap, "betweenFilePath")
    val edgesFromCenter: Array<DEdge> = loadFromFileGivenInArguments(argumentMap, "fromCenterFilePath")
    val edgesToCenter: Array<DEdge> = loadFromFileGivenInArguments(argumentMap, "toCenterFilePath")
    val salesmen: Array<DSalesman> = loadFromFileGivenInArguments(argumentMap, "salesmanFilePath")
    val objectives: Array<DObjective> = loadFromFileGivenInArguments(argumentMap, "objectivesFilePath")

    return DTask(
        salesmen = salesmen,
        costGraph = incompleteGraph.copy(
            objectives = objectives,
            edgesBetween = edgesBetween,
            edgesFromCenter = edgesFromCenter,
            edgesToCenter = edgesToCenter
        )
    )
}

fun loadArgumentsToMap(arguments: Array<String>): Map<String, String> {
    val argumentMap = mutableMapOf<String, String>()
    for (index in 0 until arguments.size / 2) {
        argumentMap[arguments[index * 2]] = arguments[index * 2 + 1]
    }
    return argumentMap
}

fun DoubleLogger.logSpecimen(name: String, specimen: ISpecimenRepresentation) {
    invoke("$name identifier: ${specimen.id} cost: ${specimen.cost}")
}
