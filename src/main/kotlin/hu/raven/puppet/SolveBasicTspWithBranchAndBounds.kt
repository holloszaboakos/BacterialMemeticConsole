package hu.raven.puppet

import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.toIntVector
import hu.raven.puppet.configuration.FilePathVariableNames
import hu.raven.puppet.logic.step.bruteforcesolver.branchAndBounds
import hu.raven.puppet.utility.KoinUtil
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path
//1300518

fun main() {
    val SIZE = 64
    startKoin {
        modules(module {
            single(named(FilePathVariableNames.INPUT_FOLDER)) { "\\input\\tsp$SIZE" }
            single(named(FilePathVariableNames.SINGLE_FILE)) { "instance0.json" }
            single(named(FilePathVariableNames.OUTPUT_FOLDER)) { Path.of("output\\default\\output.txt") }
            single<IntMatrix> {
                loadTask(
                    get(named(FilePathVariableNames.INPUT_FOLDER)),
                    get(named(FilePathVariableNames.SINGLE_FILE)),
                )
            }
        })
    }

    val task: IntMatrix = KoinUtil.get()
    val result = branchAndBounds(task)
    println(result)
}

private fun loadTask(inputFolder: String, inputFile: String): IntMatrix =
    IntMatrix(
        MatrixLoader.loadMatrix(inputFolder, inputFile)
            .map { it.toIntArray().toIntVector() }
            .toTypedArray()
    )
