package hu.raven.puppet.logic.task.loader

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.task.TaskSerializable
import java.nio.file.Path

class TspTaskLoaderService(
    override val logger: ObjectLoggerService<String>,
    private val fileName: String
) : TaskLoaderService() {
    override fun loadTask(folderPath: String): Task {
        val standardTaskSerializable: TaskSerializable = loadFromResourceFile(Path.of(folderPath, fileName))
        val standardTask: Task = standardTaskSerializable.toTask()
        val taskWithObjectives = standardTask.copy(
            costGraph = standardTask.costGraph.copy(
                objectives = Array(standardTask.costGraph.edgesFromCenter.size) {
                    CostGraphVertex()
                }.asImmutable()
            )
        )
        logEstimates(taskWithObjectives)
        return taskWithObjectives
    }

    override fun logEstimates(task: Task) {
        logOverEstimate(task)
        logUnderEstimate(task)
    }

    private fun logOverEstimate(task: Task) {
        task.costGraph.apply {
            val costOfAllFromCenterEdges = edgesFromCenter.map { it.length.value }.sumClever()
            val costOfAllToCenterEdges = edgesToCenter.map { it.length.value }.sumClever()
            val overEstimate = costOfAllFromCenterEdges + costOfAllToCenterEdges

            logger.log("OVERESTIMATE: $overEstimate")
        }
    }

    private fun logUnderEstimate(task: Task) {
        task.costGraph.apply {
            val minimalCostFromCenter = edgesFromCenter.minOfOrNull { it.length.value } ?: 0f
            val minimalCostFromTargets = edgesBetween.mapIndexed { index, edgesFrom ->
                arrayOf(
                    edgesFrom.minOfOrNull { it.length.value } ?: Float.MAX_VALUE,
                    edgesToCenter[index].length.value
                ).min()
            }.sumClever()
            val underEstimate = minimalCostFromCenter + minimalCostFromTargets
            logger.log("UNDERESTIMATE: $underEstimate")
        }
    }

}