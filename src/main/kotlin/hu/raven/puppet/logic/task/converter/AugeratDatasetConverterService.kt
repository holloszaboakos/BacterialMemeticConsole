package hu.raven.puppet.logic.task.converter

import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.Stere
import hu.raven.puppet.model.task.*
import hu.raven.puppet.model.task.augerat.InstanceBean
import hu.raven.puppet.model.task.augerat.NodeBean
import hu.raven.puppet.model.task.augerat.RequestBean
import hu.raven.puppet.utility.ImmutableArray.Companion.asImmutable
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class AugeratDatasetConverterService(override val vehicleCount: Int) : TaskConverterService<InstanceBean>() {
    override fun toStandardTask(task: InstanceBean): Task = task.run {
        Task(
            transportUnits = Array(vehicleCount) {
                TransportUnit(volumeCapacity = Stere(task.fleetBean.vehicle_profileBean.capacity.toDouble().toLong()))
            }.asImmutable(),
            costGraph = CostGraph(
                center = task.networkBean.nodeBeanList
                    .first { it.id == task.fleetBean.vehicle_profileBean.arrival_node }
                    .toGPS(),
                objectives = constructObjectives(
                    task.requestBeanList,
                    task.networkBean.nodeBeanList
                ).asImmutable(),
                edgesBetween = constructEdgesBetweenClients(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicle_profileBean.arrival_node
                )
                    .map { it.asImmutable() }
                    .toTypedArray()
                    .asImmutable(),
                edgesFromCenter = constructEdgesWithCenter(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicle_profileBean.arrival_node
                ).asImmutable(),
                edgesToCenter = constructEdgesWithCenter(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicle_profileBean.arrival_node
                ).asImmutable()
            )
        )
    }

    private fun constructObjectives(
        requests: List<RequestBean>,
        nodes: List<NodeBean>
    ): Array<CostGraphVertex> {
        return requests.map {
            CostGraphVertex(
                location = nodes
                    .first { node -> node.id == it.node }
                    .toGPS(),
                volume = Stere(it.quantity.toDouble().toLong())
            )
        }
            .toTypedArray()
    }

    private fun constructEdgesWithCenter(
        nodes: List<NodeBean>,
        centerId: String
    ): Array<CostGraphEdge> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center
        return clients.map {
            CostGraphEdge(
                length = Meter((it.toGPS() euclideanDist center.toGPS()).toLong())
            )
        }.toTypedArray()
    }

    private fun constructEdgesBetweenClients(
        nodes: List<NodeBean>,
        centerId: String
    ): Array<Array<CostGraphEdge>> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center
        return clients.map { nodeFrom ->
            clients
                .filter { it != nodeFrom }
                .map { nodeTo ->
                    CostGraphEdge(
                        length = Meter((nodeFrom.toGPS() euclideanDist nodeTo.toGPS()).toLong())
                    )
                }
                .toTypedArray()
        }.toTypedArray()
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