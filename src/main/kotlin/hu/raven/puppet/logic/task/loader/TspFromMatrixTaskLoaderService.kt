package hu.raven.puppet.logic.task.loader

import hu.akos.hollo.szabo.collections.asImmutable
import hu.raven.puppet.model.task.TspTask
import hu.raven.puppet.model.utility.math.CompleteGraph
import java.nio.file.Path

class TspFromMatrixTaskLoaderService(
    private val fileName: String,
    override val log: (String) -> Unit,
) : TaskLoaderService<TspTask>() {

    override fun loadTask(folderPath: String): TspTask {
        val task: CompleteGraph<Unit, Int> =
//            this::class.java.getResourceAsStream(
//                Path.of(folderPath, fileName).toString().replace("\\", "/").also { println(it) }
//            )
            Path.of(folderPath, fileName).toFile()
                .readLines()
                .map { it.split("\t").map { it.toFloat().toInt() } }
                .let { distanceMatrix ->
                    CompleteGraph(
                        vertices = Array(distanceMatrix.size) { }.asImmutable(),
                        edges = distanceMatrix
                            .mapIndexed { _, edgesFromSource ->
                                edgesFromSource
                                    .mapIndexed { _, weight ->
                                        weight
                                    }
                                    .toTypedArray()
                                    .asImmutable()
                            }
                            .toTypedArray()
                            .asImmutable()
                    )
                }
        logEstimates(TspTask(task))
        return TspTask(task)
    }

    override fun logEstimates(task: TspTask) {
        logOverEstimate(task.distanceMatrix)
        logUnderEstimate(task.distanceMatrix)
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
