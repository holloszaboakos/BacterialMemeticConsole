package hu.raven.puppet.logic.task.loader

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.FloatSumExtensions.preciseSum
import hu.raven.puppet.logic.task.converter.DesmetDatasetConverterService
import hu.raven.puppet.model.task.ProcessedDesmetTask
import hu.raven.puppet.model.task.desmet.*
import hu.raven.puppet.model.task.desmet.DesmetFileHeader.*
import hu.raven.puppet.model.task.desmet.DesmetFileSection.*
import java.nio.file.Path

class DesmetTaskLoaderService(
    private val converter: DesmetDatasetConverterService,
    private val fileName: String,
    override val log: (String) -> Unit,
) : TaskLoaderService<ProcessedDesmetTask>() {

    companion object {
        private const val NODE_COORDINATE_LIST_START = "NODE_COORD_SECTION"
        private const val DISTANCE_MATRIX_START = "EDGE_WEIGHT_SECTION"
        private const val NODE_DEMAND_LIST_START = "DEMAND_SECTION"
        private const val DEPOT_LIST_START = "DEPOT_SECTION"
    }

    override fun loadTask(folderPath: String): ProcessedDesmetTask {
        val desmetTask = loadDataFromFile(Path.of(folderPath, fileName))
        val standardTask = converter.processRawTask(desmetTask)
        logEstimates(standardTask)
        return standardTask
    }

    override fun logEstimates(task: ProcessedDesmetTask) {
        task.graph.apply {
            log(
                "OVERESTIMATE: ${
                    edges.asList().last().asSequence().map { it.value }.preciseSum()
                            + edges.asSequence().map { it.asList().last().value }.preciseSum()
                }"
            )

            log(
                "UNDERESTIMATE: ${
                    edges
                        .asSequence()
                        .map { edgesFromNode ->
                            edgesFromNode.asList().minOfOrNull { it.value } ?: 0f
                        }
                        .preciseSum()
                }"
            )
        }
    }

    private fun loadDataFromFile(filePath: Path): DesmetTask {
        var currentSection: DesmetFileSection = HEADER
        val mutableTask = DesmetTaskMutable()

        (this.javaClass.getResource(filePath.toString()) ?: throw Exception("Failed to open resource"))
            .openStream()
            .reader()
            .forEachLine { line ->
                currentSection = when (currentSection) {
                    HEADER -> onHeader(line, mutableTask)
                    NODE_COORDINATES -> onNodeCoordinates(line, mutableTask)
                    WEIGHT_MATRIX -> onWeightMatrix(line, mutableTask)
                    NODE_DEMAND -> onNodeDemand(line, mutableTask)
                    DEPOT -> {
                        onDepot(line, mutableTask)
                        DEPOT
                    }
                }
            }

        mutableTask.checkFormat()

        return mutableTask.toImmutable()
    }

    private fun onDepot(
        line: String,
        mutableTask: DesmetTaskMutable,
    ) {
        if (line != "-1" && line != "EOF") {
            mutableTask.depotId = line.toInt()
        }
    }

    private fun onNodeDemand(
        line: String,
        mutableTask: DesmetTaskMutable,
    ): DesmetFileSection {
        if (line == DEPOT_LIST_START) {
            return DEPOT
        }
        mutableTask.nodeDemands.add(
            DesmetDataFileLine(line).toNodeDemand()
        )
        return NODE_DEMAND
    }

    private fun onWeightMatrix(
        line: String,
        mutableTask: DesmetTaskMutable,
    ): DesmetFileSection {
        if (line == NODE_DEMAND_LIST_START) {
            return NODE_DEMAND
        }
        mutableTask.distanceMatrix.add(
            DesmetDataFileLine(line).toDistanceMatrixLine()
        )
        return WEIGHT_MATRIX
    }

    private fun onNodeCoordinates(
        line: String,
        mutableTask: DesmetTaskMutable,
    ): DesmetFileSection {
        if (line == DISTANCE_MATRIX_START) {
            return WEIGHT_MATRIX
        }
        mutableTask.nodeCoordinates.add(
            DesmetDataFileLine(line).toNodeCoordinate()
        )
        return NODE_COORDINATES
    }

    private fun onHeader(
        line: String,
        mutableTask: DesmetTaskMutable,
    ): DesmetFileSection {
        if (line == NODE_COORDINATE_LIST_START) {
            return NODE_COORDINATES
        }
        DesmetDataFileLine(line).toHeader().also {
            when (it.first) {
                COMMENT_HEADER -> mutableTask.comments.add(it.second)
                DIMENSION_HEADER -> mutableTask.dimension = it.second.toInt()
                CAPACITY_HEADER -> mutableTask.capacity = it.second.toInt()
                NAME_HEADER -> mutableTask.name = it.second
                TYPE_HEADER -> mutableTask.type = it.second
                EDGE_WEIGHT_TYPE_HEADER -> mutableTask.edgeWeightType = it.second
                EDGE_WEIGHT_FORMAT_HEADER -> mutableTask.edgeWeightFormat = it.second
                EDGE_WEIGHT_UNIT_OF_MEASUREMENT_HEADER -> {
                    mutableTask.edgeWeightUnitOfMeasurement = it.second
                }
            }
        }
        return HEADER
    }

    private fun DesmetTaskMutable.checkFormat() {
        val nodeCoordinateIds = nodeCoordinates
            .map { it.nodeId }
            .toSet()

        val nodeDemandIds = nodeDemands
            .map { it.nodeId }
            .toSet()

        if (
            dimension <= 0 ||
            capacity <= 0 ||
            nodeCoordinates.size != dimension ||
            nodeDemands.size != dimension ||
            nodeCoordinateIds.size != dimension ||
            nodeDemandIds.size != dimension ||
            nodeCoordinateIds.intersect(nodeDemandIds).size != dimension ||
            !nodeCoordinateIds.contains(depotId)
        ) {
            throw Exception("Desment Task is malformed!")
        }

    }

    private fun DesmetTaskMutable.toImmutable() = DesmetTask(
        name = name,
        comments = comments.toTypedArray().asImmutable(),
        type = type,
        dimension = dimension,
        edgeWeightType = edgeWeightType,
        edgeWeightFormat = edgeWeightFormat,
        edgeWeightUnitOfMeasurement = edgeWeightUnitOfMeasurement,
        capacity = capacity,
        nodeCoordinates = nodeCoordinates.toTypedArray().asImmutable(),
        distanceMatrix = DistanceMatrix(distanceMatrix.toTypedArray().asImmutable()),
        nodeDemands = nodeDemands.associateBy { it.nodeId },
        depotId = depotId
    )
}