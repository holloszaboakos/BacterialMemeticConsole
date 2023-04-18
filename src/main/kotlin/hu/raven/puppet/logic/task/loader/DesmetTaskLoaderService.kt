package hu.raven.puppet.logic.task.loader

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.task.converter.DesmetDatasetConverterService
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.task.desmet.*
import hu.raven.puppet.model.task.desmet.DesmetFileHeader.*
import hu.raven.puppet.utility.ImmutableArray.Companion.asImmutable
import hu.raven.puppet.utility.extention.sumClever
import java.nio.file.Path

class DesmetTaskLoaderService(
    override val logger: ObjectLoggerService<String>,
    private val converter: DesmetDatasetConverterService,
    private val fileName: String,
) : TaskLoaderService() {

    companion object {
        private const val NODE_COORDINATE_LIST_START = "NODE_COORD_SECTION"
        private const val DISTANCE_MATRIX_START = "EDGE_WEIGHT_SECTION"
        private const val NODE_DEMAND_LIST_START = "DEMAND_SECTION"
        private const val DEPOT_LIST_START = "DEPOT_SECTION"
    }

    override fun loadTask(folderPath: String): Task {
        val desmetTask = loadDataFromFile(Path.of(folderPath, fileName))
        val standardTask = converter.toStandardTask(desmetTask)
        logEstimates(standardTask)
        return standardTask
    }

    override fun logEstimates(task: Task) {
        task.costGraph.apply {
            logger.log(
                "OVERASTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.sumClever()
                                    + edgesToCenter.map { it.length.value }.sumClever()
                            )
                }"
            )

            logger.log(
                "UNDERASTIMATE: ${
                    (
                            edgesFromCenter.map { it.length.value }.min() +
                                    edgesBetween.mapIndexed { index, edge ->
                                        arrayOf(
                                            edge.minOfOrNull { it.length.value }!!,
                                            edgesToCenter[index].length.value
                                        ).min()
                                    }.sumClever()
                            )
                }"
            )
        }
    }

    private fun loadDataFromFile(filePath: Path): DesmetTask {
        var currentSection: DesmetFileSection = DesmetFileSection.HEADER
        val mutableTask = DesmetTaskMutable()

        this.javaClass.getResource(filePath.toString())!!.openStream().reader().forEachLine { line ->
            when (currentSection) {
                DesmetFileSection.HEADER -> {
                    if (line == NODE_COORDINATE_LIST_START) {
                        currentSection = DesmetFileSection.NODE_COORDINATES
                        return@forEachLine
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
                }

                DesmetFileSection.NODE_COORDINATES -> {
                    if (line == DISTANCE_MATRIX_START) {
                        currentSection = DesmetFileSection.WEIGHT_MATRIX
                        return@forEachLine
                    }
                    mutableTask.nodeCoordinates.add(
                        DesmetDataFileLine(line).toNodeCoordinate()
                    )
                }

                DesmetFileSection.WEIGHT_MATRIX -> {
                    if (line == NODE_DEMAND_LIST_START) {
                        currentSection = DesmetFileSection.NODE_DEMAND
                        return@forEachLine
                    }
                    mutableTask.distanceMatrix.add(
                        DesmetDataFileLine(line).toDistanceMatrixLine()
                    )
                }

                DesmetFileSection.NODE_DEMAND -> {
                    if (line == DEPOT_LIST_START) {
                        currentSection = DesmetFileSection.DEPOT
                        return@forEachLine
                    }
                    mutableTask.nodeDemands.add(
                        DesmetDataFileLine(line).toNodeDemand()
                    )
                }

                DesmetFileSection.DEPOT -> {
                    if (line == "-1" || line == "EOF") {
                        return@forEachLine
                    }
                    mutableTask.depotId = line.toInt()
                }
            }

        }

        mutableTask.checkFormat()

        return mutableTask.toImmutable()
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