package hu.raven.puppet.logic.logging

import java.nio.file.Path

class ObjectLoggerService<T : Any>(override val outputPath: Path) : AlgorithmLoggerService<T> {

    override fun initFile() = outputPath.toFile().run {
        if (!exists()) {
            createNewFile()
        }
    }

    override fun log(data: T) {
        println(data)
        outputPath.toFile().appendText("$data\n")
    }
}