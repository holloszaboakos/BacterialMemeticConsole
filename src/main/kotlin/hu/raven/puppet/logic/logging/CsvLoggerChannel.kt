package hu.raven.puppet.logic.logging

import hu.raven.puppet.model.logging.LogEvent
import hu.raven.puppet.model.logging.LogType
import java.nio.file.Files
import java.time.LocalDateTime
import kotlin.io.path.Path

class CsvLoggerChannel<T>(
    private val outputFolder: List<String>,
    private val separator: String,
    private val mapping: Map<String, (T) -> String>,
    private val type: LogType,
    private val name: String,
    private val version: Int,
    outputFileName: String,
) : LoggingChannel<T> {
    private val outputFile = Path("", *outputFolder.toTypedArray(), "$outputFileName.json").toFile()
    private val defaultHeaders = arrayOf("time", "type", "source", "version")
    override fun initialize() {
        val folderPath = Path("", *outputFolder.toTypedArray())
        if (!Files.exists(folderPath)) {
            folderPath.toFile().mkdirs()
        }
        outputFile.writeText("${(defaultHeaders + mapping.keys).joinToString(separator)}\n")
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
        return mapping.values
            .map { it(message.message) }
            .let {
                arrayOf(
                    message.time.toString(),
                    message.type.name,
                    message.source,
                    message.version.toString(),
                ) + it
            }
            .joinToString(separator)
    }
}