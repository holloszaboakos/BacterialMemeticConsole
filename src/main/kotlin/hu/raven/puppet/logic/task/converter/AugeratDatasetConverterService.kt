package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.physics.CubicMeter
import hu.akos.hollo.szabo.physics.Meter
import hu.raven.puppet.model.task.*
import hu.raven.puppet.model.task.augerat.InstanceBean
import hu.raven.puppet.model.task.augerat.NodeBean
import hu.raven.puppet.model.task.augerat.RequestBean
import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.CompleteGraphEdge
import hu.raven.puppet.model.utility.math.CompleteGraphVertex
import hu.raven.puppet.model.utility.math.CompleteGraphWithCenterVertex
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class AugeratDatasetConverterService : TaskConverterService<InstanceBean,ProcessedAugeratTask>() {
    override fun processRawTask(task: InstanceBean): ProcessedAugeratTask = task.run {
        ProcessedAugeratTask(
            capacity = task.fleetBean.vehicleProfileBean.capacity.toDouble().toInt(),
            graph = CompleteGraphWithCenterVertex(
                centerVertex = task.networkBean.nodeBeanList
                    .first { it.id == task.fleetBean.vehicleProfileBean.arrivalNode }
                    .toGPS(),
                vertices = constructObjectives(
                    task.requestBeanList,
                    task.networkBean.nodeBeanList
                ),
                edgesBetween = constructEdgesBetweenClients(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicleProfileBean.arrivalNode
                ),
                edgesFromCenter = constructEdgesFromCenter(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicleProfileBean.arrivalNode
                ),
                edgesToCenter = constructEdgesToCenter(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicleProfileBean.arrivalNode
                )
            )
        )
    }

    private fun constructObjectives(
        requests: List<RequestBean>,
        nodes: List<NodeBean>
    ): ImmutableArray<CompleteGraphVertex<LocationWithVolume>> {
        return requests.map {
            LocationWithVolume(
                location = nodes
                    .first { node -> node.id == it.node }
                    .toGPS(),
                volume = it.quantity.toDouble().toInt()
            )
        }
            .mapIndexed { index, value -> CompleteGraphVertex(index, value) }
            .toTypedArray()
            .asImmutable()
    }

    private fun constructEdgesFromCenter(
        nodes: List<NodeBean>,
        centerId: String
    ): ImmutableArray<CompleteGraphEdge<Float>> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center

        return clients.mapIndexed { fromIndex, nodeTo ->
            CompleteGraphEdge(
                fromIndex = fromIndex,
                toIndex = nodes.lastIndex,
                value = (center.toGPS() euclideanDist nodeTo.toGPS())
            )
        }.toTypedArray()
            .asImmutable()
    }

    private fun constructEdgesToCenter(
        nodes: List<NodeBean>,
        centerId: String
    ): ImmutableArray<CompleteGraphEdge<Float>> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center
        return clients.mapIndexed { toIndex, nodeTo ->
            CompleteGraphEdge(
                fromIndex = nodes.lastIndex,
                toIndex = toIndex,
                value = (center.toGPS() euclideanDist nodeTo.toGPS())
            )
        }.toTypedArray()
            .asImmutable()
    }

    private fun constructEdgesBetweenClients(
        nodes: List<NodeBean>,
        centerId: String
    ): ImmutableArray<ImmutableArray<CompleteGraphEdge<Float>>> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center
        return clients.mapIndexed {fromIndex, nodeFrom ->
            clients
                .mapIndexed { toIndex, nodeTo ->
                    CompleteGraphEdge(
                        fromIndex = fromIndex,
                        toIndex = toIndex,
                        value = (nodeFrom.toGPS() euclideanDist nodeTo.toGPS())
                    )
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