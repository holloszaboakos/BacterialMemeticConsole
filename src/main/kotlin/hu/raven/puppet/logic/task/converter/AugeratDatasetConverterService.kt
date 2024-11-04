package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.Gps
import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.raven.puppet.model.task.LocationWithVolume
import hu.raven.puppet.model.task.ProcessedAugeratTask
import hu.raven.puppet.model.task.augerat.InstanceBean
import hu.raven.puppet.model.task.augerat.NodeBean
import hu.raven.puppet.model.task.augerat.RequestBean
import hu.raven.puppet.model.utility.math.CompleteGraph
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

data object AugeratDatasetConverterService : TaskConverterService<InstanceBean, ProcessedAugeratTask>() {
    override fun processRawTask(task: InstanceBean): ProcessedAugeratTask = task.run {
        ProcessedAugeratTask(
            capacity = task.fleetBean.vehicleProfileBean.capacity.toDouble().toInt(),
            graph = CompleteGraph(
                vertices = constructObjectives(
                    task.requestBeanList,
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicleProfileBean.arrivalNode
                ),
                edges = constructEdges(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicleProfileBean.arrivalNode
                ),
            )
        )
    }

    private fun constructObjectives(
        requests: List<RequestBean>,
        nodes: List<NodeBean>,
        arrivalNodeId: String
    ): ImmutableArray<LocationWithVolume> {
        return requests.map {
            LocationWithVolume(
                location = nodes
                    .first { node -> node.id == it.node }
                    .toGPS(),
                volume = it.quantity.toDouble().toInt()
            )
        }
            .plus(
                LocationWithVolume(
                    location = nodes
                        .first { it.id == arrivalNodeId }
                        .toGPS(),
                    volume = 0
                )
            )
            .toTypedArray()
            .asImmutable()
    }

    private fun constructEdges(
        nodes: List<NodeBean>,
        centerId: String
    ): ImmutableArray<ImmutableArray<Float>> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center + center
        return clients.mapIndexed { _, nodeFrom ->
            clients
                .mapIndexed { _, nodeTo ->
                    nodeFrom.toGPS() euclideanDist nodeTo.toGPS()
                }
                .toTypedArray()
                .asImmutable()
        }.toTypedArray()
            .asImmutable()
    }

    private fun NodeBean.toGPS(): Gps = Gps(
        latitude = cx.toFloat(),
        longitude = cy.toFloat()
    )

    private infix fun Gps.euclideanDist(other: Gps): Float = sqrt(
        (latitude * 1000 - other.latitude * 1000).pow(2) +
                (longitude * 1000 - other.longitude * 1000).pow(2)
    ).let { max(1f, it) }
}