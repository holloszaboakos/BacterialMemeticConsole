package hu.raven.puppet.logic.task.converter

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.raven.puppet.model.task.LocationWithVolume
import hu.raven.puppet.model.task.ProcessedAugeratTask
import hu.raven.puppet.model.task.augerat.InstanceBean
import hu.raven.puppet.model.task.augerat.NodeBean
import hu.raven.puppet.model.task.augerat.RequestBean
import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.model.utility.math.GraphVertex
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class AugeratDatasetConverterService : TaskConverterService<InstanceBean, ProcessedAugeratTask>() {
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
    ): ImmutableArray<GraphVertex<LocationWithVolume>> {
        return requests.map {
            LocationWithVolume(
                location = nodes
                    .first { node -> node.id == it.node }
                    .toGPS(),
                volume = it.quantity.toDouble().toInt()
            )
        }
            .mapIndexed { index, value -> GraphVertex(index, value) }
            .plus(
                GraphVertex(
                    index = nodes.lastIndex,
                    value = LocationWithVolume(
                        location = nodes
                            .first { it.id == arrivalNodeId }
                            .toGPS(),
                        volume = 0
                    )
                )
            )
            .toTypedArray()
            .asImmutable()
    }

    private fun constructEdges(
        nodes: List<NodeBean>,
        centerId: String
    ): ImmutableArray<ImmutableArray<GraphEdge<Float>>> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center + center
        return clients.mapIndexed { fromIndex, nodeFrom ->
            clients
                .mapIndexed { toIndex, nodeTo ->
                    GraphEdge(
                        sourceNodeIndex = fromIndex,
                        targetNodeIndex = toIndex,
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