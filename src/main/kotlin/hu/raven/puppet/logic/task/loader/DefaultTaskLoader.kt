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
            val salesman = task.salesmen.first()

            doubleLogger("OVERASTIMATE: ${
                edgesFromCenter.sumOf { calcCostOnEdge(salesman, it).value.toDouble() }
                        + edgesToCenter.sumOf { calcCostOnEdge(salesman, it).value.toDouble() }
                        + objectives.sumOf { calcCostOnNode(salesman, it).value.toDouble() }
            }")

            doubleLogger("UNDERASTIMATE: ${
                edgesFromCenter.minOf { calcCostOnEdge(salesman, it).value.toDouble() }
                        + edgesBetween.sumOf { edgeArray ->
                    min(
                        edgeArray.values.minOf { calcCostOnEdge(salesman, it).value.toDouble() },
                        calcCostOnEdge(salesman, edgesToCenter[edgeArray.orderInOwner]).value.toDouble()
                    )
                }
                        + objectives.sumOf { calcCostOnNode(salesman, it).value.toDouble() }
            }")
        }
    }

    private fun calcCostOnEdge(salesman: DSalesman, edge: DEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: DSalesman, objective: DObjective) =
        salesman.salary * objective.time
}