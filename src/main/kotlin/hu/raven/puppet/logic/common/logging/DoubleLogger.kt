package hu.raven.puppet.logic.common.logging

import java.io.File

class DoubleLogger(var targetFile: File) {
    operator fun invoke(message: String) {
        println(message)
        targetFile.appendText("$message\n")
    }
}