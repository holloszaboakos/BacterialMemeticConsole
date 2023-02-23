package hu.raven.puppet.logic.logging

import java.io.File

class DoubleLogger() : AlgorithmLogger() {
    operator fun invoke(message: String) {
        val targetFile = File("$outputFolderPath\\$targetFileName.txt")
        println(message)
//TODO        targetFile.appendText("$message\n")
    }
}