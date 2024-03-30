package hu.raven.puppet.job.experiments

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.logging.LogEvent
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.utility.LocalDateTimeTypeAdapter
import hu.raven.puppet.utility.LocalDateTypeAdapter
import hu.raven.puppet.utility.PermutationTypeAdapter
import net.lingala.zip4j.ZipFile
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val gson = GsonBuilder()
    .registerTypeAdapter(Permutation::class.java, PermutationTypeAdapter)
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter)
    .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter)
    .create()

fun main() {
    val day = LocalDate.of(2024, 3, 26)
    extractLogsOfDay(day)
    val extracted = extractConfigAndScoreDataOfADay(day)
    val output = File("D:\\Research\\Results\\extractions\\costTimeSeries.json")
    if (!output.exists()) {
        output.writeText("")
    }
    extracted.forEach { record ->
        output.appendText(gson.toJson(record) + "\n")
    }
    deleteExtractedLogsOfDay(day)
}

fun extractLogsOfDay(localDate: LocalDate) {
    val zipFile = ZipFile("D:\\Research\\Results\\${localDate.format(DateTimeFormatter.ISO_DATE)}.zip")
    zipFile.extractAll("D:\\Research\\TEMP")
}

fun extractConfigAndScoreDataOfADay(localDate: LocalDate): List<Pair<Configuration, Map<Long, Float>>> {
    val folderOfDay = File("D:\\TEMP\\${localDate.format(DateTimeFormatter.ISO_DATE)}")

    val result = folderOfDay.listFiles()
        .map { folderOfRun ->
            println(folderOfRun)
            val configTypeToken = object : TypeToken<LogEvent<Configuration>>() {}.type
            val config = folderOfRun.resolve("config.json").readText()
                .let { gson.fromJson<LogEvent<Configuration>>(it, configTypeToken) }
                .message

            val costRecords = folderOfRun.resolve("cost.json").useLines { lines ->
                var score = Float.MAX_VALUE

                lines
                    .associate {
                        val typeToken = object :
                            TypeToken<LogEvent<Pair<OnePartRepresentationWithCostAndIterationAndId, List<Float>>>>() {}.type
                        val log: LogEvent<Pair<OnePartRepresentationWithCostAndIterationAndId, List<Float>>> =
                            gson.fromJson(it, typeToken)

                        Pair(log.time, log.message.second.first())
                    }
                    .mapValues { (_, value) ->
                        if (value > score) {
                            score
                        } else {
                            score = value
                            value
                        }
                    }
                    .let {
                        val firstMoment = it.keys.first().atZone(ZoneId.systemDefault()).toEpochSecond()
                        it.mapKeys { (key, _) ->
                            key.atZone(ZoneId.systemDefault()).toEpochSecond() - firstMoment
                        }
                    }
            }
            Pair(config, costRecords)
        }

    println(result)
    return result
}

fun deleteExtractedLogsOfDay(localDate: LocalDate) {
    val folderOfDay = File("D:\\Research\\TEMP\\${localDate.format(DateTimeFormatter.ISO_DATE)}")
    folderOfDay.deleteRecursively()
}