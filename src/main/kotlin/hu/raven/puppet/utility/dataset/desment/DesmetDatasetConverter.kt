package hu.raven.puppet.utility.dataset.desment

import hu.raven.puppet.model.dataset.desmet.Task
import hu.raven.puppet.model.dataset.desmet.graph.NodeCoordinate
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.task.*
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject

object DesmetDatasetConverter {
    val vehicleCount: Int by inject(AlgorithmParameters.VEHICLE_COUNT)

    fun toStandardTask(task: Task): hu.raven.puppet.model.task.Task = task.run {
        val depot = nodeCoordinates.first { it.nodeId == depotId }

        return Task(
            transportUnits = Array(vehicleCount) {
                TransportUnit(
                    volumeCapacity = Stere(capacity.toLong())
                )
            },
            costGraph = CostGraph(
                center = depot.toGPS(),
                objectives = buildObjectives(),
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

    private fun Task.buildObjectives(): Array<CostGraphVertex> {
        val targetNodes = nodeCoordinates.filter { it.nodeId != depotId }

        return targetNodes
            .map {
                CostGraphVertex(
                    location = it.toGPS(),
                    volume = Stere(nodeDemands.getValue(it.nodeId).demand.toLong())
                )
            }.toTypedArray()
    }

    private fun Task.buildEdgesToCenter(): Array<CostGraphEdge> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { indexValuePair ->
                val weight = distanceMatrix.distances[indexValuePair.index][depotIndex]
                CostGraphEdge(length = Meter(weight.times(1000).toLong()))
            }
            .toTypedArray()
    }

    private fun Task.buildEdgesFromCenter(): Array<CostGraphEdge> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { indexValuePair ->
                val weight = distanceMatrix.distances[depotIndex][indexValuePair.index]
                CostGraphEdge(length = Meter(weight.toLong()))
            }
            .toTypedArray()

    }

    private fun Task.buildEdgesBetween(): Array<Array<CostGraphEdge>> {
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { fromNodeIndexed ->
                targetNodesWithIndex
                    .filter { it.index != fromNodeIndexed.index }
                    .map { toNodeIndexed ->
                        val weight = distanceMatrix.distances[fromNodeIndexed.index][toNodeIndexed.index]
                        CostGraphEdge(length = Meter(weight.times(1000).toLong()))
                    }
                    .toTypedArray()
            }
            .toTypedArray()
    }

}