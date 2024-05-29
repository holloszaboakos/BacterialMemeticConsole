package hu.raven.puppet.job

import hu.raven.puppet.logic.task.generator.TspGeneratorService
import java.io.File

fun main() {
    listOf(100, 300, 500, 700, 1000).forEach { problemSize ->
        val fileDir = File("output\\tsp$problemSize")
        fileDir.mkdir()
        repeat(10) { instanceIndex ->
            val generator = TspGeneratorService()
            val tspTask = generator.generateTspTask(problemSize, 1..(1_000_000 * problemSize / 50))
            val outputFile = File("output\\tsp$problemSize\\instance$instanceIndex.txt")
            outputFile.createNewFile()
            tspTask.edges.forEach { edgesWithSameSource ->
                edgesWithSameSource.asSequence()
                    .joinToString(" ")
                    .let { outputFile.appendText("$it\n") }
            }
        }
    }
}