package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.min
import hu.raven.puppet.utility.extention.sumClever

class TspTaskLoader(
    val taskSize: Int,
    val versionIndex: Int
) : TaskLoader() {
    override fun loadTask(folderPath: String): Task {
        TODO("RANDOM GENERATION AND STABILIZATION")
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

            doubleLogger("OVERASTIMATE: $overEstimate")
        }
    }

    private fun logUnderEstimate(task: Task) {
        task.costGraph.apply {
            val minimalCostFromCenter = edgesFromCenter.map { it.length.value }.min()
            val minimalCostFromTargets = edgesBetween.mapIndexed { index, edgesFrom ->
                arrayOf(
                    edgesFrom.map { it.length.value }.min(),
                    edgesToCenter[index].length.value
                ).min()
            }.sumClever()
            val underEstimate = minimalCostFromCenter + minimalCostFromTargets
            doubleLogger("UNDERASTIMATE: $underEstimate")
        }
    }

}