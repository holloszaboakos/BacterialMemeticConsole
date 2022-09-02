package hu.raven.puppet

import com.google.gson.Gson
import hu.raven.puppet.logic.AlgorithmManagerService
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.modules.bacterialModule
import hu.raven.puppet.logic.modules.commonModule
import hu.raven.puppet.logic.modules.commonPostModule
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.logging.BacterialMemeticAlgorithmLogLine
import hu.raven.puppet.model.logging.PopulationData
import hu.raven.puppet.model.logging.ProgressData
import hu.raven.puppet.model.logging.SpecimenData
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DEdgeArray
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.model.task.graph.DObjective
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


@ExperimentalTime
fun main(arguments: Array<String>) {
    startKoin {
        modules(commonModule)
        modules(bacterialModule)
        modules(commonPostModule)
    }
    val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    //val diversity: Diversity by inject(Diversity::class.java)

    val argumentMap = loadArgumentsToMap(arguments)

    val outputFolderPath = argumentMap["-outputFolderPath"] ?: throw Error("No path given")
    val time = LocalDateTime.now().toString().split('.')[0].replace(':', '-')
    val outputFile = File("$outputFolderPath\\statistics-$time.txt")
    logger.targetFile = outputFile

    val fullRuntime = measureTime {
        val algorithmManager: AlgorithmManagerService by inject(AlgorithmManagerService::class.java)
        algorithmManager.run {
            task = loadTask(argumentMap)
            checkIfTaskIsWellFormatted()
            initializeAlgorithm()

            val gson = Gson()
            var timeElapsedTotal = Duration.ZERO
            var fitnessCallCountOld = 0L
            for (index in 0 until algorithm.iterationLimit) {
                val timeElapsed = measureTime {
                    runIteration()
                }

                timeElapsedTotal += timeElapsed


                val logData = calculateLogData(
                    algorithm,
                    statistics,
                    fitnessCallCountOld,
                    timeElapsed,
                    timeElapsedTotal
                )

                fitnessCallCountOld = statistics.fitnessCallCount

                logger(gson.toJson(logData))

                println()

                if (statistics.fitnessCallCount > 5_000_000) {
                    break
                }
            }
        }
    }

    logger("FULL RUNTIME: $fullRuntime")

}

fun <S : ISpecimenRepresentation> calculateLogData(
    algorithm: SEvolutionaryAlgorithm<S>,
    statistics: BacterialAlgorithmStatistics,
    fitnessCallCountOld: Long,
    timeElapsed: Duration,
    timeElapsedTotal: Duration
): BacterialMemeticAlgorithmLogLine {
    val best = algorithm.copyOfBest!!
    val second =
        if (algorithm.population.size > 1)
            algorithm.population[1]
        else null
    val third =
        if (algorithm.population.size > 2)
            algorithm.population[2]
        else null
    val worst = algorithm.copyOfWorst!!
    val median = algorithm.population.median()

    val logOfBest = best.toLog()
    val logOfSecond = second?.toLog()
    val logOfThird = third?.toLog()
    val logOfWorst = worst.toLog()
    val logOfMedian = median.toLog()

    return BacterialMemeticAlgorithmLogLine(
        ProgressData(
            generation = algorithm.iteration + 1,
            timeTotal = timeElapsedTotal,
            timeOfIteration = timeElapsed,
            fitnessCallCountSoFar = statistics.fitnessCallCount,
            fitnessCallCountOfIteration = statistics.fitnessCallCount - fitnessCallCountOld //TODO
        ),
        PopulationData(
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

fun ISpecimenRepresentation.toLog() = SpecimenData(id, cost)

fun <T> ArrayList<T>.median() = get(size / 2)

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
