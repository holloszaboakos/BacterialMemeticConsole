package hu.raven.puppet.utility.desment

import hu.raven.puppet.model.desmettask.Task
import hu.raven.puppet.model.desmettask.graph.NodeCoordinate
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.model.task.graph.*

object DesmetDatasetConverter {
    fun toStandardTask(task: Task): DTask = task.run {
        val depot = nodeCoordinates.first { it.nodeId == depotId }

        return DTask(
            name = name,
            salesmen = Array(dimension) {
                DSalesman(
                    volumeCapacity_Stere = capacity.toLong()
                )
            },
            costGraph = DGraph(
                center = depot.toGPS(),
                objectives = buildObjectives(),
                edgesFromCenter = buildEdgesFromCenter(),
                edgesToCenter = buildEdgesToCenter(),
                edgesBetween = buildEdgesBetween()
            )
        )
    }

    private fun NodeCoordinate.toGPS(): DGps {
        return DGps(
            latitude = firstCoordinate.toFloat(),
            longitude = secondCoordinate.toFloat(),
        )
    }

    private fun Task.buildObjectives(): Array<DObjective> {
        val targetNodes = nodeCoordinates.filter { it.nodeId != depotId }

        return targetNodes
            .map {
                DObjective(
                    name = it.nodeId.toString(),
                    location = it.toGPS(),
                    volume_Stere = nodeDemands.getValue(it.nodeId).demand.toLong()
                )
            }.toTypedArray()
    }

    private fun Task.buildEdgesToCenter(): Array<DEdge> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { indexValuePair ->
                val weight = distanceMatrix.distances[indexValuePair.index][depotIndex]
                DEdge(length_Meter = weight.times(1000).toLong())
            }
            .toTypedArray()
    }

    private fun Task.buildEdgesFromCenter(): Array<DEdge> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { indexValuePair ->
                val weight = distanceMatrix.distances[depotIndex][indexValuePair.index]
                DEdge(length_Meter = weight.toLong())
            }
            .toTypedArray()

    }

    private fun Task.buildEdgesBetween(): Array<DEdgeArray> {
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { fromNodeIndexed ->
                DEdgeArray(
                    values = targetNodesWithIndex
                        .filter { it.index != fromNodeIndexed.index }
                        .map { toNodeIndexed ->
                            val weight = distanceMatrix.distances[fromNodeIndexed.index][toNodeIndexed.index]
                            DEdge(length_Meter = weight.times(1000).toLong())
                        }
                        .toTypedArray()
                )
            }
            .toTypedArray()
    }

}