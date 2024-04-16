package hu.raven.puppet.job

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.job.experiments.BacteriophageAlgorithmConfiguration
import hu.raven.puppet.model.logging.LogEvent
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.utility.LocalDateTimeTypeAdapter
import hu.raven.puppet.utility.LocalDateTypeAdapter
import hu.raven.puppet.utility.PermutationTypeAdapter
import net.lingala.zip4j.ZipFile
import java.io.File
import java.time.*
import java.time.format.DateTimeFormatter

private val gson = GsonBuilder()
    .registerTypeAdapter(Permutation::class.java, PermutationTypeAdapter)
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter)
    .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter)
    .create()

fun main() {
    listOf(
        LocalDate.of(2024, 4, 14),
    )
        .forEach { date ->
            zipProcessing(
                day = date,
                inputPostFix = "",
                outputFolder = File("D:\\Research\\Results3\\extractions\\costTimeSeries"),
                //outputFile = File("output.json"),
                resultsPath = "D:\\Research\\Results3",
                tempPath = "D:\\Research\\TEMP"
            )
        }
}

fun zipProcessing(
    day: LocalDate,
    inputPostFix: String,
    outputFolder: File,
    resultsPath: String,
    tempPath: String,
) {
    extractLogsOfDay(
        day,
        inputPostFix,
        resultsPath,
        tempPath
    )
    if (!outputFolder.exists()) {
        outputFolder.mkdir()
    }

    extractConfigAndScoreDataOfADay(day, tempPath) { folderOfRun, config, entries ->
        //outputFile.appendText(gson.toJson(record))
        val folderOfOutput = File("${outputFolder.path}\\${folderOfRun.name}")
        folderOfOutput.mkdir()

        val configFile = File("${folderOfOutput}\\configuration.json")
        configFile.writeText(gson.toJson(config))

        val timeSeriesFile = File("${folderOfOutput}\\timeSeries.json")
        timeSeriesFile.writeText("")
        entries.forEach { value ->
            timeSeriesFile.appendText("${gson.toJson(value)}\n")
        }
    }
    deleteExtractedLogsOfDay(day, tempPath)
}

fun extractLogsOfDay(
    localDate: LocalDate,
    inputPostFix: String,
    resultsPath: String,
    tempPath: String,
) {
    val zipFile = ZipFile("$resultsPath\\${localDate.format(DateTimeFormatter.ISO_DATE)}$inputPostFix.zip")
    zipFile.extractAll(tempPath)
}

fun extractConfigAndScoreDataOfADay(
    localDate: LocalDate,
    tempPath: String,
    onConfigAndTimeSeriesEntry: (File, BacteriophageAlgorithmConfiguration, Sequence<List<Long>>) -> Unit,
) {
    val folderOfDay = File("$tempPath\\${localDate.format(DateTimeFormatter.ISO_DATE)}")

    folderOfDay.listFiles()
        .asSequence()
        .dropWhile { !it.name.contains("2024-04-14T11_09_24_183362500") }
        .forEach { folderOfRun ->
            println(folderOfRun)
            val configTypeToken = object : TypeToken<LogEvent<BacteriophageAlgorithmConfiguration>>() {}.type
            val config = folderOfRun.resolve("config.json").readText()
                .let { gson.fromJson<LogEvent<BacteriophageAlgorithmConfiguration>>(it, configTypeToken) }
                .message

            val costRecords = folderOfRun.resolve("cost.json")
                .useLines { lines ->
                    var score = Float.MAX_VALUE

                    lines
                        .map {
                            val typeToken = object :
                                TypeToken<LogEvent<Pair<OnePartRepresentationWithCostAndIterationAndId, List<Float>>>>() {}.type
                            val log: LogEvent<Pair<OnePartRepresentationWithCostAndIterationAndId, List<Float>>> =
                                gson.fromJson(it, typeToken)

                            Pair(log.time, log.message.second.first())
                        }
                        .map { (key, value) ->
                            if (value > score) {
                                Pair(key, score)
                            } else {
                                score = value
                                Pair(key, value)
                            }
                        }
                        .let {
                            var firstMoment: ZonedDateTime = LocalDateTime
                                .of(1, 1, 1, 1, 1)
                                .atZone(ZoneId.systemDefault())
                            it
                                .mapIndexed { index, (key, value) ->
                                    if (index == 0) firstMoment = key.atZone(ZoneId.systemDefault())
                                    val diffInNanos =
                                        Duration.between(firstMoment, key.atZone(ZoneId.systemDefault())).toNanos()
                                    listOf(diffInNanos, value.toLong())
                                }
                        }
                        .let {
                            onConfigAndTimeSeriesEntry(folderOfRun, config, it)
                        }
                }
            Pair(config, costRecords)
        }
}

fun deleteExtractedLogsOfDay(
    localDate: LocalDate,
    tempPath: String,
) {
    val folderOfDay = File("$tempPath\\${localDate.format(DateTimeFormatter.ISO_DATE)}")
    folderOfDay.deleteRecursively()
}