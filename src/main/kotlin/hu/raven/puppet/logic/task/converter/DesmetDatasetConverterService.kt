package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.physics.CubicMeter
import hu.akos.hollo.szabo.physics.Meter
import hu.raven.puppet.model.task.*
import hu.raven.puppet.model.task.desmet.DesmetTask
import hu.raven.puppet.model.task.desmet.NodeCoordinate
import hu.raven.puppet.model.utility.Gps

class DesmetDatasetConverterService(override val vehicleCount: Int) : TaskConverterService<DesmetTask>() {

    override fun toStandardTask(task: DesmetTask): Task = task.run {
        val depot = nodeCoordinates.first { it.nodeId == depotId }

        return Task(
            transportUnits = Array(vehicleCount) {
                TransportUnit(
                    volumeCapacity = CubicMeter(capacity)
                )
            },
            costGraph = CostGraph(
                center = depot.toGPS(),
                objectives = buildObjectives(),
                edgesBetween = buildEdgesBetween()
                    .map { it }
                    .toTypedArray()
                    ,
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

    private fun DesmetTask.buildObjectives(): Array<CostGraphVertex> {
        val targetNodes = nodeCoordinates.filter { it.nodeId != depotId }

        return targetNodes
            .map {
                CostGraphVertex(
                    location = it.toGPS(),
                    volume = CubicMeter(nodeDemands.getValue(it.nodeId).demand)
                )
            }.toTypedArray()
    }

    private fun DesmetTask.buildEdgesToCenter(): Array<CostGraphEdge> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { indexValuePair ->
                val weight = distanceMatrix.distances[indexValuePair.index][depotIndex]
                CostGraphEdge(length = Meter(weight.times(1000).toFloat()))
            }
            .toTypedArray()
    }

    private fun DesmetTask.buildEdgesFromCenter(): Array<CostGraphEdge> {
        val depotIndex = nodeCoordinates
            .indexOfFirst { it.nodeId == depotId }
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { indexValuePair ->
                val weight = distanceMatrix.distances[depotIndex][indexValuePair.index]
                CostGraphEdge(length = Meter(weight.toFloat()))
            }
            .toTypedArray()

    }

    private fun DesmetTask.buildEdgesBetween(): Array<Array<CostGraphEdge>> {
        val targetNodesWithIndex = nodeCoordinates
            .withIndex()
            .filter { it.value.nodeId != depotId }

        return targetNodesWithIndex
            .map { fromNodeIndexed ->
                targetNodesWithIndex
                    .filter { it.index != fromNodeIndexed.index }
                    .map { toNodeIndexed ->
                        val weight = distanceMatrix.distances[fromNodeIndexed.index][toNodeIndexed.index]
                        CostGraphEdge(length = Meter(weight.times(1000).toFloat()))
                    }
                    .toTypedArray()
            }
            .toTypedArray()
    }

}