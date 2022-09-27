package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DEdgeArray
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.model.task.graph.DObjective
import hu.raven.puppet.modules.FilePathVariableNames
import kotlin.math.min

class DefaultTaskLoader : TaskLoader() {
    override fun loadTak(folderPath: String): DTask {
        val incompleteGraph: DGraph =
            loadFromResourceFile(folderPath, FilePathVariableNames.GRAPH_FILE)
        val edgesBetween: Array<DEdgeArray> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_BETWEEN_FILE)
        val edgesFromCenter: Array<DEdge> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_FROM_CENTER_FILE)
        val edgesToCenter: Array<DEdge> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_TO_CENTER_FILE)
        val salesmen: Array<DSalesman> =
            loadFromResourceFile(folderPath, FilePathVariableNames.SALESMAN_FILE)
        val objectives: Array<DObjective> =
            loadFromResourceFile(folderPath, FilePathVariableNames.OBJECTIVES_FILE)

        val task = DTask(
            salesmen = salesmen,
            costGraph = incompleteGraph.copy(
                objectives = objectives,
                edgesBetween = edgesBetween,
                edgesFromCenter = edgesFromCenter,
                edgesToCenter = edgesToCenter
            )
        )

        if (!task.isWellFormatted()) {
            throw Exception("Task is wrongly formatted!")
        }

        logEstimates(task)

        return task

    }

    override fun logEstimates(task: DTask) {
        task.costGraph.apply {
            doubleLogger("OVERASTIMATE: ${
                edgesFromCenter.sumOf { it.length_Meter }
                        + edgesToCenter.sumOf { it.length_Meter }
            }")

            doubleLogger("UNDERASTIMATE: ${
                edgesFromCenter.minOf { it.length_Meter } +
                        edgesBetween.sumOf { edge->
                            min(
                                edge.values.minOf { it.length_Meter },
                                edgesToCenter[edge.orderInOwner].length_Meter
                            )
                        }
            }")
        }
    }
}