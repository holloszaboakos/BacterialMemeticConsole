package hu.raven.puppet.logic.logging

import com.google.gson.GsonBuilder
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.logging.LogEvent
import hu.raven.puppet.model.logging.LogType
import hu.raven.puppet.utility.LocalDateTimTypeAdapter
import hu.raven.puppet.utility.PermutationTypeAdapter
import java.nio.file.Files
import java.text.DateFormat
import java.time.LocalDateTime
import kotlin.io.path.Path

class JsonChannel<T>(
    private val outputFolder: List<String>,
    private val type: LogType,
    private val name: String,
    private val version: Int,
    outputFileName: String,
) : LoggingChannel<T> {
    private val parser = GsonBuilder()
        .registerTypeAdapter(Permutation::class.java, PermutationTypeAdapter)
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimTypeAdapter)
        .setDateFormat(DateFormat.FULL)
        .create()
    private val outputFile = Path("", *outputFolder.toTypedArray(), "$outputFileName.json").toFile()
    override fun initialize() {
        val folderPath = Path("", *outputFolder.toTypedArray())
        if (!Files.exists(folderPath)) {
            folderPath.toFile().mkdirs()
        }
        outputFile.writeText("")
    }

    override fun send(message: T) {
        val event = LogEvent(
            time = LocalDateTime.now(),
            type = type,
            source = name,
            version = version,
            message = message
        )
        outputFile.appendText("${toString(event)}\n")
    }

    override fun toString(message: LogEvent<T>): String {
        return parser.toJson(message)
    }
}