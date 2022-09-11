package hu.raven.puppet.logic.logging

import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.inject
import org.koin.core.logger.Logger
import java.io.File
import java.time.LocalDateTime

class DoubleLogger() : AlgorithmLogger() {
    override val targetFile: File = File("$outputFolderPath\\statistics-$creationTime.txt")

    operator fun invoke(message: String) {
        println(message)
        targetFile.appendText("$message\n")
    }
}