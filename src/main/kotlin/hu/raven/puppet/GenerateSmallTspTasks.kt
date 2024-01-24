package hu.raven.puppet

import com.google.gson.GsonBuilder
import hu.raven.puppet.logic.task.generator.TspGeneratorService
import hu.raven.puppet.model.task.TaskSerializable
import java.io.File

private val SIZES_TO_GENERATE = intArrayOf(4, 8, 16, 32, 64, 128, 256, 512, 1024)
private const val REPEAT_PER_SIZE = 10
private val DISTANCE_RANGE = 1..<10_000
private const val OUTPUT_FOLDER = "output/generated_tsp_tasks"

fun main() {
    val generatorService = TspGeneratorService()
    val gson = GsonBuilder().setPrettyPrinting().create()
    for (size in SIZES_TO_GENERATE) {
        repeat(REPEAT_PER_SIZE) { repetitionIndex ->
            val task = generatorService.generateTspTask(size, DISTANCE_RANGE)
            val json = gson.toJson(TaskSerializable(task))
            val outputFile = File("$OUTPUT_FOLDER\\size${size}instance$repetitionIndex.txt")
            outputFile.writeText(json)
            println("finished generation of size${size}instance$repetitionIndex.txt")
        }
    }
}

