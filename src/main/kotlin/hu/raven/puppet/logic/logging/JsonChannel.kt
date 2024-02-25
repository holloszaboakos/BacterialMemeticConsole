package hu.raven.puppet.logic.logging

import com.google.gson.Gson
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

class JsonChannel<T>(
    private val outputFolder: Array<String>,
    private val outputFileName: String,
) : LoggingChannel<T> {
    private val parser = Gson()
    override fun initialize() {
        val folderPath = Path("", *outputFolder)
        if (!Files.exists(folderPath)) {
            folderPath.toFile().mkdirs()
        }
        Path("", *outputFolder, "$outputFileName.json").toFile()
            .writeText("")
    }

    override fun send(message: T) {
        Path("", *outputFolder, "$outputFileName.json").toFile()
            .appendText("${toString(message)}\n")
    }

    override fun toString(message: T): String {
        return parser.toJson(message)
    }
}