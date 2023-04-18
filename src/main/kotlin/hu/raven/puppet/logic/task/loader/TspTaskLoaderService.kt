package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.model.task.CostGraphVertex
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.ImmutableArray.Companion.asImmutable
import hu.raven.puppet.utility.extention.sumClever
import java.nio.file.Path

class TspTaskLoaderService(
    override val logger: ObjectLoggerService<String>,
    private val fileName: String
) : TaskLoaderService() {
    override fun loadTask(folderPath: String): Task {
        val standardTask: Task = loadFromResourceFile(Path.of(folderPath, fileName))
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

            logger.log("OVERASTIMATE: $overEstimate")
        }
    }

    private fun logUnderEstimate(task: Task) {
        task.costGraph.apply {
            val minimalCostFromCenter = edgesFromCenter.minOfOrNull { it.length.value }!!
            val minimalCostFromTargets = edgesBetween.mapIndexed { index, edgesFrom ->
                arrayOf(
                    edgesFrom.minOfOrNull { it.length.value }!!,
                    edgesToCenter[index].length.value
                ).min()
            }.sumClever()
            val underEstimate = minimalCostFromCenter + minimalCostFromTargets
            logger.log("UNDERASTIMATE: $underEstimate")
        }
    }

}