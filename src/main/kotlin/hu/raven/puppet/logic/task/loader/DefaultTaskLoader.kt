package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.model.task.*
import hu.raven.puppet.modules.FilePathVariableNames
import hu.raven.puppet.utility.extention.min
import hu.raven.puppet.utility.extention.sumClever

class DefaultTaskLoader : TaskLoader() {
    override fun loadTask(folderPath: String): Task {
        val incompleteGraph: CostGraph =
            loadFromResourceFile(folderPath, FilePathVariableNames.GRAPH_FILE)
        val edgesBetween: Array<Array<CostGraphEdge>> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_BETWEEN_FILE)
        val edgesFromCenter: Array<CostGraphEdge> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_FROM_CENTER_FILE)
        val edgesToCenter: Array<CostGraphEdge> =
            loadFromResourceFile(folderPath, FilePathVariableNames.EDGES_TO_CENTER_FILE)
        val salesmen: Array<TransportUnit> =
            loadFromResourceFile(folderPath, FilePathVariableNames.SALESMAN_FILE)
        val objectives: Array<CostGraphVertex> =
            loadFromResourceFile(folderPath, FilePathVariableNames.OBJECTIVES_FILE)

        val task = Task(
            transportUnits = salesmen,
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

    override fun logEstimates(task: Task) {
        task.costGraph.apply {
            val salesman = task.transportUnits.first()

            doubleLogger(
                "OVERASTIMATE: ${
                    (edgesFromCenter.map { calcCostOnEdge(salesman, it).value }.sumClever()
                            + edgesToCenter.map { calcCostOnEdge(salesman, it).value }.sumClever()
                            + objectives.map { calcCostOnNode(salesman, it).value }.sumClever())
                }"
            )

            doubleLogger(
                "UNDERASTIMATE: ${
                    (edgesFromCenter.map { calcCostOnEdge(salesman, it).value }.min()
                            + edgesBetween.mapIndexed { index, edgeArray ->
                        arrayOf(
                            edgeArray.map { calcCostOnEdge(salesman, it).value }.sumClever(),
                            calcCostOnEdge(salesman, edgesToCenter[index]).value
                        ).min()
                    }.min()
                            + objectives.map { calcCostOnNode(salesman, it).value }.sumClever())
                }"
            )
        }
    }

    private fun calcCostOnEdge(salesman: TransportUnit, edge: CostGraphEdge) =
        salesman.fuelPrice * salesman.fuelConsumption * edge.length +
                salesman.salary * (edge.length / salesman.vehicleSpeed)

    private fun calcCostOnNode(salesman: TransportUnit, objective: CostGraphVertex) =
        salesman.salary * objective.time
}