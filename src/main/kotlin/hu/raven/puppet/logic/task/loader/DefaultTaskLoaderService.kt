package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.model.task.*
import hu.raven.puppet.utility.ImmutableArray
import hu.raven.puppet.utility.ImmutableArray.Companion.asImmutable
import hu.raven.puppet.utility.extention.FloatSumExtensions.sumClever
import java.nio.file.Path

//TODO fix json structures
class DefaultTaskLoaderService(
    private val graphFilePath: String,
    private val edgesBetweenFilePath: String,
    private val edgesFromFilePath: String,
    private val edgesToFilePath: String,
    private val salesmenFilePath: String,
    private val objectivesFilePath: String,
    override val logger: ObjectLoggerService<String>
) : TaskLoaderService() {
    override fun loadTask(folderPath: String): Task {
        val incompleteGraph: CostGraph =
            loadFromResourceFile(Path.of(folderPath, graphFilePath))
        val edgesBetween: ImmutableArray<ImmutableArray<CostGraphEdge>> =
            loadFromResourceFile<Array<Array<CostGraphEdge>>>(Path.of(folderPath, edgesBetweenFilePath))
                .map { it.asImmutable() }
                .toTypedArray()
                .asImmutable()
        val edgesFromCenter: ImmutableArray<CostGraphEdge> =
            loadFromResourceFile<Array<CostGraphEdge>>(Path.of(folderPath, edgesFromFilePath)).asImmutable()
        val edgesToCenter: ImmutableArray<CostGraphEdge> =
            loadFromResourceFile<Array<CostGraphEdge>>(Path.of(folderPath, edgesToFilePath)).asImmutable()
        val salesmen: ImmutableArray<TransportUnit> =
            loadFromResourceFile<Array<TransportUnit>>(Path.of(folderPath, salesmenFilePath)).asImmutable()
        val objectives: ImmutableArray<CostGraphVertex> =
            loadFromResourceFile<Array<CostGraphVertex>>(Path.of(folderPath, objectivesFilePath)).asImmutable()

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

            logger.log(
                "OVERESTIMATE: ${
                    (edgesFromCenter.map { calcCostOnEdge(salesman, it).value }.sumClever()
                            + edgesToCenter.map { calcCostOnEdge(salesman, it).value }.sumClever()
                            + objectives.map { calcCostOnNode(salesman, it).value }.sumClever())
                }"
            )

            logger.log(
                "UNDERESTIMATE: ${
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