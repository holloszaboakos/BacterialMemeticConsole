package hu.raven.puppet.logic.logging

import java.io.File

class DoubleLogger() : AlgorithmLogger() {
    override val targetFile: File = File("$outputFolderPath\\statistics-$creationTime.txt")

    operator fun invoke(message: String) {
        println(message)
        targetFile.appendText("$message\n")
    }
}