package hu.raven.puppet.logic.logging

import java.nio.file.Files
import kotlin.io.path.Path

class CsvLoggerChannel<T>(
    private val outputFolder: Array<String>,
    private val outputFileName: String,
    private val separator: String,
    private val mapping: Map<String, (T) -> String>,
) : LoggingChannel<T> {
    override fun initialize() {
        val folderPath = Path("", *outputFolder)
        if (!Files.exists(folderPath)) {
            folderPath.toFile().mkdirs()
        }
        Path("", *outputFolder, "$outputFileName.csv").toFile()
            .writeText("${mapping.keys.joinToString(separator)}\n")
    }

    override fun send(message: T) {
        Path("", *outputFolder, "$outputFileName.csv").toFile()
            .appendText("${toString(message)}\n")
    }

    override fun toString(message: T): String {
        return mapping.values.joinToString(separator) { it(message) }
    }
}