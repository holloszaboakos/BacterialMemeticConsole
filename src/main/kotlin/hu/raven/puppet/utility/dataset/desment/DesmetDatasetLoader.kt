package hu.raven.puppet.utility.dataset.desment

import hu.raven.puppet.model.dataset.desmet.DataFileLine
import hu.raven.puppet.model.dataset.desmet.Task
import hu.raven.puppet.model.dataset.desmet.graph.DistanceMatrix
import hu.raven.puppet.model.dataset.desmet.graph.NodeCoordinate
import hu.raven.puppet.model.dataset.desmet.graph.NodeDemand
import hu.raven.puppet.utility.dataset.desment.DesmetFileHeader.*
import hu.raven.puppet.utility.dataset.desment.DesmetFileSection.*
import java.io.File

object DesmetDatasetLoader {

    private const val NODE_COORDINATE_LIST_START = "NODE_COORD_SECTION"
    private const val DISTANCE_MATRIX_START = "EDGE_WEIGHT_SECTION"
    private const val NODE_DEMAND_LIST_START = "DEMAND_SECTION"
    private const val DEPOT_LIST_START = "DEPOT_SECTION"

    fun loadDataFromFile(filePath: String): Task {
        var currentSection: DesmetFileSection = HEADER
        val mutableTask = TaskMutable()

        File(filePath).forEachLine { line ->
            when (currentSection) {
                HEADER -> {
                    if (line == NODE_COORDINATE_LIST_START) {
                        currentSection = NODE_COORDINATES
                        return@forEachLine
                    }
                    DataFileLine(line).toHeader().also {
                        mutableTask.apply {
                            when (it.first) {
                                NAME_HEADER ->
                                    name = it.second
                                COMMENT_HEADER ->
                                    comments.add(it.second)
                                TYPE_HEADER ->
                                    type = it.second
                                DIMENSION_HEADER ->
                                    dimension = it.second.toInt()
                                EDGE_WEIGHT_TYPE_HEADER ->
                                    edgeWeightType = it.second
                                EDGE_WEIGHT_FORMAT_HEADER ->
                                    edgeWeightFormat = it.second
                                EDGE_WEIGHT_UNIT_OF_MEASUREMENT_HEADER ->
                                    edgeWeightUnitOfMeasurement = it.second
                                CAPACITY_HEADER ->
                                    capacity = it.second.toInt()
                            }
                        }
                    }
                }
                NODE_COORDINATES -> {
                    if (line == DISTANCE_MATRIX_START) {
                        currentSection = WEIGHT_MATRIX
                        return@forEachLine
                    }
                    mutableTask.nodeCoordinates.add(
                        DataFileLine(line).toNodeCoordinate()
                    )
                }
                WEIGHT_MATRIX -> {
                    if (line == NODE_DEMAND_LIST_START) {
                        currentSection = NODE_DEMAND
                        return@forEachLine
                    }
                    mutableTask.distanceMatrix.add(
                        DataFileLine(line).toDistanceMatrixLine()
                    )
                }
                NODE_DEMAND -> {
                    if (line == DEPOT_LIST_START) {
                        currentSection = DEPOT
                        return@forEachLine
                    }
                    mutableTask.nodeDemands.add(
                        DataFileLine(line).toNodeDemand()
                    )
                }
                DEPOT -> {
                    if (line == "-1" || line == "EOF") {
                        return@forEachLine
                    }
                    mutableTask.depotId = line.toInt()
                }
            }

        }

        mutableTask.checkFormat()

        return mutableTask.toTask()
    }

    private fun DataFileLine.toHeader(): Pair<DesmetFileHeader, String> {
        val lineParts = line
            .split(':', limit = 2)
            .map { it.trim() }

        return Pair(
            DesmetFileHeader.values()
                .first { it.tag == lineParts[0] },
            lineParts[1]
        )
    }

    private fun DataFileLine.toNodeCoordinate(): NodeCoordinate {
        val lineParts = line
            .split(' ', limit = 4)
            .map { it.trim() }

        return NodeCoordinate(
            nodeId = lineParts[0].toInt(),
            firstCoordinate = lineParts[1].toDouble(),
            secondCoordinate = lineParts[2].toDouble(),
            nameString = lineParts[3]
        )
    }

    private fun DataFileLine.toDistanceMatrixLine(): DoubleArray {
        return line
            .split(' ')
            .filter { it.isNotBlank() }
            .map { it.toDouble() }
            .toDoubleArray()
    }

    private fun DataFileLine.toNodeDemand(): NodeDemand {
        val lineParts = line
            .split(' ', limit = 2)
            .map { it.trim().toInt() }

        return NodeDemand(
            nodeId = lineParts[0],
            demand = lineParts[1],
        )
    }

    private fun TaskMutable.checkFormat() {
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
            throw Exception("Desment Task is wrongly Formatted!")
        }

    }

    private fun TaskMutable.toTask(): Task {
        return Task(
            name = name,
            comments = comments.toTypedArray(),
            type = type,
            dimension = dimension,
            edgeWeightType = edgeWeightType,
            edgeWeightFormat = edgeWeightFormat,
            edgeWeightUnitOfMeasurement = edgeWeightUnitOfMeasurement,
            capacity = capacity,
            nodeCoordinates = nodeCoordinates.toTypedArray(),
            distanceMatrix = DistanceMatrix(distanceMatrix.toTypedArray()),
            nodeDemands = nodeDemands.associateBy { it.nodeId },
            depotId = depotId
        )
    }
}