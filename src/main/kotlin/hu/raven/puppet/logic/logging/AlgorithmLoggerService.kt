package hu.raven.puppet.logic.logging

import java.nio.file.Path

interface AlgorithmLoggerService<T : Any> {
    val outputPath: Path
    fun initFile()
    fun log(data: T)
}
