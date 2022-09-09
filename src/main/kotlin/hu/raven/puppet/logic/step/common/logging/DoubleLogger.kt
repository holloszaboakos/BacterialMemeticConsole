package hu.raven.puppet.logic.step.common.logging

import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject
import java.io.File
import java.time.LocalDateTime

class DoubleLogger() {
    private val targetFile: File

    init {
        val outputFolderPath: String by inject(FilePathVariableNames.OUTPUT_FOLDER)
        val time = LocalDateTime.now().toString().split('.')[0].replace(':', '-')
        val outputFile = File("$outputFolderPath\\statistics-$time.txt")
        targetFile = outputFile
    }

    operator fun invoke(message: String) {
        println(message)
        targetFile.appendText("$message\n")
    }
}