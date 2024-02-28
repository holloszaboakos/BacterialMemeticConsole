package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.physics.Second
import hu.raven.puppet.model.task.*
import hu.raven.puppet.model.task.desmet.DesmetTask
import hu.raven.puppet.model.task.desmet.NodeCoordinate
import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.model.utility.math.GraphVertex
import hu.raven.puppet.model.utility.math.CompleteGraphWithCenterVertex

class DesmetDatasetConverterService : TaskConverterService<DesmetTask,ProcessedDesmetTask>() {

    override fun processRawTask(task: DesmetTask): ProcessedDesmetTask = task.run {
        val depot = nodeCoordinates.first { it.nodeId == depotId }

        return ProcessedDesmetTask(
            capacity = capacity,
            graph = CompleteGraphWithCenterVertex(
                centerVertex = depot.toGPS(),
                vertices = buildObjectives(),
                edgesBetween = buildEdgesBetween(),
                edgesFromCenter = buildEdgesFromCenter(),
                edgesToCenter = buildEdgesToCenter()
            )
        )
    }

    private fun NodeCoordinate.toGPS(): Gps {
        return Gps(
            latitude = firstCoordinate.toFloat(),
            longitude = secondCoordinate.toFloat(),
        )
    }

    private fun DesmetTask.buildObjectives(): ImmutableArray<GraphVertex<LocationWithVolumeAndName>> {
        val targetNodes = nodeCoordinates.filter { it.nodeId != depotId }

        return targetNodes
            .map {
                LocationWithVolumeAndName(
                    location = it.toGPS(),
                    volume = nodeDemands.getValue(it.nodeId).demand,
                    name = it.nameString
                )
            }
            .mapIndexed { index, value -> GraphVertex(index, value) }
            .toTypedArray()
            .asImmutable()
    }

    private fun DesmetTask.buildEdgesToCenter(): ImmutableArray<GraphEdge<Second>> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .mapIndexed {newIndex, (oldIndex,_) ->
                val weight = distanceMatrix.distances[oldIndex][depotIndex]
                GraphEdge(
                    sourceNodeIndex = newIndex,
                    targetNodeIndex = nodeCoordinates.withIndex().last().index,
                    value = Second(weight.toFloat())
                )
            }
            .toTypedArray()
            .asImmutable()
    }

    private fun DesmetTask.buildEdgesFromCenter(): ImmutableArray<GraphEdge<Second>> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .mapIndexed {newIndex, (oldIndex,_) ->
                val weight = distanceMatrix.distances[depotIndex][oldIndex]
                GraphEdge(
                    sourceNodeIndex = nodeCoordinates.withIndex().last().index,
                    targetNodeIndex = newIndex,
                    value = Second(weight.toFloat())
                )
            }
            .toTypedArray()
            .asImmutable()

    }

    private fun DesmetTask.buildEdgesBetween(): ImmutableArray<ImmutableArray<GraphEdge<Second>>> {
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .mapIndexed { newFromIndex, (oldFromNodeIndexed, _) ->
                targetNodesWithIndex
                    .mapIndexed { newToIndex, (oldToNodeIndexed, _) ->
                        val weight = distanceMatrix.distances[oldFromNodeIndexed][oldToNodeIndexed]
                        GraphEdge(
                            sourceNodeIndex = newFromIndex,
                            targetNodeIndex = newToIndex,
                            value = Second(weight.toFloat())
                        )
                    }
                    .toTypedArray()
                    .asImmutable()
            }
            .toTypedArray()
            .asImmutable()
    }

}