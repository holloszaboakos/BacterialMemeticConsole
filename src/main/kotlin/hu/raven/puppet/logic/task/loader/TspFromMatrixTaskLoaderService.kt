package hu.raven.puppet.logic.task.loader

import hu.akos.hollo.szabo.collections.asImmutable
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.model.utility.math.GraphVertex
import java.nio.file.Path

class TspFromMatrixTaskLoaderService(
    private val fileName: String,
    override val log: (String) -> Unit,
) : TaskLoaderService<CompleteGraph<Unit, Int>>() {

    override fun loadTask(folderPath: String): CompleteGraph<Unit, Int> {
        val task: CompleteGraph<Unit, Int> =
            this::class.java.getResourceAsStream(Path.of(folderPath, fileName).toString().replace("\\","/").also { println(it) })
                .let { it ?: throw Exception("Resource not found!") }
                .bufferedReader()
                .lines()
                .map { it.split("\t").map { it.toFloat().toInt() } }.toList().let { distanceMatrix ->
                    CompleteGraph(vertices = Array(distanceMatrix.size) {
                        GraphVertex(
                            index = it,
                            value = Unit
                        )
                    }.asImmutable(),
                        edges = distanceMatrix
                            .mapIndexed { sourceIndex, edgesFromSource ->
                                edgesFromSource
                                    .mapIndexed { targetIndex, weight ->
                                        GraphEdge(
                                            sourceNodeIndex = sourceIndex,
                                            targetNodeIndex = targetIndex,
                                            value = weight
                                        )
                                    }
                                    .toTypedArray()
                                    .asImmutable()
                            }
                            .toTypedArray()
                            .asImmutable()
                    )
                }
        logEstimates(task)
        return task
    }

    override fun logEstimates(task: CompleteGraph<Unit, Int>) {
        logOverEstimate(task)
        logUnderEstimate(task)
    }

    private fun logOverEstimate(task: CompleteGraph<Unit, Int>) {
        task.apply {
            val costOfAllFromCenterEdges = edges.last().map { it.value }.sum()
            val costOfAllToCenterEdges = edges.map { it.last().value }.sum()
            val overEstimate = costOfAllFromCenterEdges + costOfAllToCenterEdges

            log("OVERESTIMATE: $overEstimate")
        }
    }

    private fun logUnderEstimate(task: CompleteGraph<Unit, Int>) {
        task.apply {

            val underEstimate = edges.map { it.minOfOrNull { it.value } ?: 0 }
            log("UNDERESTIMATE: $underEstimate")
        }
    }

}
