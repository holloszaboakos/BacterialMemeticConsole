package hu.raven.puppet.logic.task.loader

import hu.akos.hollo.szabo.collections.asImmutable
import hu.raven.puppet.model.utility.math.CompleteGraph
import java.nio.file.Path

class TspFromMatrixTaskLoaderService(
    private val fileName: String,
    override val log: (String) -> Unit,
) : TaskLoaderService<CompleteGraph<Unit, Int>>() {

    override fun loadTask(folderPath: String): CompleteGraph<Unit, Int> {
        val task: CompleteGraph<Unit, Int> =
            this::class.java.getResourceAsStream(
                Path.of(folderPath, fileName).toString().replace("\\", "/").also { println(it) })
                .let { it ?: throw Exception("Resource not found!") }
                .bufferedReader()
                .lines()
                .map { it.split("\t").map { it.toFloat().toInt() } }.toList().let { distanceMatrix ->
                    CompleteGraph(vertices = Array(distanceMatrix.size) { Unit }.asImmutable(),
                        edges = distanceMatrix
                            .mapIndexed { sourceIndex, edgesFromSource ->
                                edgesFromSource
                                    .mapIndexed { targetIndex, weight ->
                                        weight
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
            val costOfAllFromCenterEdges = edges.last().map { it }.sum()
            val costOfAllToCenterEdges = edges.map { it.last() }.sum()
            val overEstimate = costOfAllFromCenterEdges + costOfAllToCenterEdges

            log("OVERESTIMATE: $overEstimate")
        }
    }

    private fun logUnderEstimate(task: CompleteGraph<Unit, Int>) {
        task.apply {

            val underEstimate = edges
                .mapIndexed { indexOuter, it ->
                    it.asSequence()
                        .filterIndexed { indexInner, _ -> indexOuter != indexInner }
                        .minOfOrNull { it }
                        ?: 0
                }
                .sum()
            log("UNDERESTIMATE: $underEstimate")
        }
    }

}
