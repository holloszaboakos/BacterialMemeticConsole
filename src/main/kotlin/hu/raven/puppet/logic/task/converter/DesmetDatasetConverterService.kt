package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.Gps
import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.physics.Second
import hu.raven.puppet.model.dataset.LocationWithVolumeAndName
import hu.raven.puppet.model.dataset.ProcessedDesmetTask
import hu.raven.puppet.model.dataset.augerat.desmet.DesmetTask
import hu.raven.puppet.model.dataset.augerat.desmet.NodeCoordinate
import hu.raven.puppet.model.utility.math.CompleteGraph

data object DesmetDatasetConverterService : TaskConverterService<DesmetTask, ProcessedDesmetTask>() {

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
            .mapIndexed { _, oldFromNodeIndexed ->
                nodeIndexes
                    .mapIndexed { _, oldToNodeIndexed ->
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