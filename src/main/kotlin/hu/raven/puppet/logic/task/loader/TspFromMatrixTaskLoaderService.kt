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
//            this::class.java.getResourceAsStream(
//                Path.of(folderPath, fileName).toString().replace("\\", "/").also { println(it) }
//            )
            Path.of(folderPath, fileName).toFile()
                .readLines()
                .map { it.split("\t").map { it.toFloat().toInt() } }
                .let { distanceMatrix ->
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
            val costOfAllFromCenterEdges = edges.asList().last().asSequence().map { it }.sum()
            val costOfAllToCenterEdges = edges.asSequence().map { it.asList().last() }.sum()
            val overEstimate = costOfAllFromCenterEdges + costOfAllToCenterEdges

            log("OVERESTIMATE: $overEstimate")
        }
    }

    private fun logUnderEstimate(task: CompleteGraph<Unit, Int>) {
        task.apply {

            val underEstimate = edges
                .asSequence()
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
