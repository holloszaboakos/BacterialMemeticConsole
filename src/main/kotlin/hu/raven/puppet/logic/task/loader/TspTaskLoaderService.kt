package hu.raven.puppet.logic.task.loader

import hu.akos.hollo.szabo.collections.asImmutable
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.MutableCompleteGraph
import java.nio.file.Path

class TspTaskLoaderService(
    private val fileName: String,
    override val log: (String) -> Unit,
) : TaskLoaderService<CompleteGraph<Unit, Int>>() {

    override fun loadTask(folderPath: String): CompleteGraph<Unit, Int> {
        val taskSerializable: MutableCompleteGraph<Unit, Int> = loadFromResourceFile(Path.of(folderPath, fileName))
        val task = taskSerializable.toImmutable()
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

            val underEstimate = edges.asSequence().map { it.asList().minOfOrNull { it } ?: 0 }.sum()
            log("UNDERESTIMATE: $underEstimate")
        }
    }

}

private fun <V, E> MutableCompleteGraph<V, E>.toImmutable(): CompleteGraph<V, E> {
    return CompleteGraph(
        vertices = vertices.asImmutable(),
        edges = edges.map { it.asImmutable() }.toTypedArray().asImmutable()
    )
}
