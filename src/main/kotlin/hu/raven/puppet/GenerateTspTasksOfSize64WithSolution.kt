package hu.raven.puppet

import hu.raven.puppet.logic.task.generator.TspGeneratorService
import hu.raven.puppet.model.task.CostGraph
import java.io.File

private const val SIZE = 64
private const val AMOUNT_TO_GENERATE = 100
private val DISTANCE_RANGE = 1..<1_000_000
private const val OUTPUT_FOLDER = "output/generated_tsp_tasks_64"
fun main() {
    val generatorService = TspGeneratorService()
    repeat(AMOUNT_TO_GENERATE) { repetitionIndex ->
        val task = generatorService.generateTspTask(SIZE, DISTANCE_RANGE)
        val matrix = convertToMatrix(task.costGraph)
        val outputFile = File("$OUTPUT_FOLDER\\instance$repetitionIndex.json")
        val text = matrix.joinToString("\n") { it.joinToString("\t") }
        outputFile.writeText(text)
        println("finished generation of size${SIZE}instance$repetitionIndex.json")
    }
}

private fun convertToMatrix(costGraph: CostGraph): List<List<Int>> =
    buildList {
        add(
            buildList {
                add(0)
                addAll(costGraph.edgesFromCenter.map { it.length.value.toInt() })
            }
        )
        costGraph.edgesFromCenter.indices
            .map { index ->
                buildList {
                    add(costGraph.edgesFromCenter[index].length.value.toInt())
                    addAll(
                        costGraph.edgesBetween[index]
                            .map { it.length.value.toInt() }
                            .slice(0..<index)
                    )
                    add(0)
                    addAll(
                        costGraph.edgesBetween[index]
                            .map { it.length.value.toInt() }
                            .slice(index..<costGraph.edgesBetween[index].size)
                    )
                }
            }
            .let {
                addAll(it)
            }
    }