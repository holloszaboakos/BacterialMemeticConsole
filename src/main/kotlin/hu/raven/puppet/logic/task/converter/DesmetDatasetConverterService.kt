package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.physics.Second
import hu.raven.puppet.model.task.LocationWithVolumeAndName
import hu.raven.puppet.model.task.ProcessedDesmetTask
import hu.raven.puppet.model.task.desmet.DesmetTask
import hu.raven.puppet.model.task.desmet.NodeCoordinate
import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.model.utility.math.GraphVertex

class DesmetDatasetConverterService : TaskConverterService<DesmetTask, ProcessedDesmetTask>() {

    override fun processRawTask(task: DesmetTask): ProcessedDesmetTask = task.run {
        val depot = nodeCoordinates.asList().first { it.nodeId == depotId }

        return ProcessedDesmetTask(
            capacity = capacity,
            graph = CompleteGraph(
                vertices = buildObjectives(depot.toGPS()),
                edges = buildEdgesBetween(),
            )
        )
    }

    private fun NodeCoordinate.toGPS(): Gps {
        return Gps(
            latitude = firstCoordinate.toFloat(),
            longitude = secondCoordinate.toFloat(),
        )
    }

    private fun DesmetTask.buildObjectives(depotLocation: Gps): ImmutableArray<LocationWithVolumeAndName> {


        return nodeCoordinates
            .asSequence()
            .filter { it.nodeId != depotId }
            .map {
                LocationWithVolumeAndName(
                    location = it.toGPS(),
                    volume = nodeDemands.getValue(it.nodeId).demand,
                    name = it.nameString
                )
            }
            .plus(
                LocationWithVolumeAndName(
                    location = depotLocation,
                    volume = 0,
                    name = "DEPOT"
                )
            )
            .toList()
            .toTypedArray()
            .asImmutable()
    }

    private fun DesmetTask.buildEdgesBetween(): ImmutableArray<ImmutableArray<Second>> {
        val depotIndex = nodeCoordinates
            .asList()
            .indexOfFirst { it.nodeId == depotId }
        val targetNodeIndexes = nodeCoordinates
            .asSequence()
            .withIndex()
            .filter { it.value.nodeId != depotId }
            .map { it.index }
            .toList()

        val nodeIndexes = targetNodeIndexes + depotIndex

        return nodeIndexes
            .mapIndexed { newFromIndex, oldFromNodeIndexed ->
                nodeIndexes
                    .mapIndexed { newToIndex, oldToNodeIndexed ->
                        val weight = distanceMatrix.distances[oldFromNodeIndexed][oldToNodeIndexed]
                        Second(weight.toFloat())
                    }
                    .toTypedArray()
                    .asImmutable()
            }
            .toTypedArray()
            .asImmutable()
    }

}