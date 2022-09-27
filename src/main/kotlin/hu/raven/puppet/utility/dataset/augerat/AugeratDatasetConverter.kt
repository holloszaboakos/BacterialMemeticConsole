package hu.raven.puppet.utility.dataset.augerat

import hu.raven.puppet.model.dataset.augerat.InstanceBean
import hu.raven.puppet.model.dataset.augerat.NodeBean
import hu.raven.puppet.model.dataset.augerat.RequestBean
import hu.raven.puppet.model.task.DSalesman
import hu.raven.puppet.model.task.DTask
import hu.raven.puppet.model.task.graph.*
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

object AugeratDatasetConverter {
    private val vehicleCount : Int by inject(AlgorithmParameters.VEHICLE_COUNT)
    fun toStandardTask(task: InstanceBean): DTask = task.run {
        DTask(
            name = task.infoBean.name,
            salesmen = Array(vehicleCount) {
                DSalesman(volumeCapacity_Stere = task.fleetBean.vehicle_profileBean.capacity.toDouble().toLong())
            },
            costGraph = DGraph(
                center = task.networkBean.nodeBeanList
                    .first { it.id == task.fleetBean.vehicle_profileBean.arrival_node }
                    .toGPS(),
                objectives = constructObjectives(
                    task.requestBeanList,
                    task.networkBean.nodeBeanList
                ),
                edgesFromCenter = constructEdgesWithCenter(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicle_profileBean.arrival_node
                ),
                edgesToCenter = constructEdgesWithCenter(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicle_profileBean.arrival_node
                ),
                edgesBetween = constructEdgesBetweenClients(
                    task.networkBean.nodeBeanList,
                    task.fleetBean.vehicle_profileBean.arrival_node
                )
            )
        )
    }

    private fun constructObjectives(
        requests: List<RequestBean>,
        nodes: List<NodeBean>
    ): Array<DObjective> {
        return requests.map {
            DObjective(
                name = it.id,
                location = nodes
                    .first { node -> node.id == it.node }
                    .toGPS(),
                volume_Stere = it.quantity.toDouble().toLong()
            )
        }
            .toTypedArray()
    }

    private fun constructEdgesWithCenter(
        nodes: List<NodeBean>,
        centerId: String
    ): Array<DEdge> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center
        return clients.map {
            DEdge(
                name = it.id,
                length_Meter = ((it.toGPS() eucledienDist center.toGPS())).toLong()
            )
        }.toTypedArray()
    }

    private fun constructEdgesBetweenClients(
        nodes: List<NodeBean>,
        centerId: String
    ): Array<DEdgeArray> {
        val center = nodes.first { it.id == centerId }
        val clients = nodes - center
        return clients.map { nodeFrom ->
            DEdgeArray(
                values = clients
                    .filter { it != nodeFrom }
                    .map { nodeTo ->
                        DEdge(
                            name = "${nodeFrom.id} to ${nodeTo.id}",
                            length_Meter = ((nodeFrom.toGPS() eucledienDist nodeTo.toGPS())).toLong()
                        )
                    }
                    .toTypedArray()
            )
        }.toTypedArray()
    }

    private fun NodeBean.toGPS(): DGps = DGps(
        latitude = cx.toFloat(),
        longitude = cy.toFloat()
    )

    private infix fun DGps.eucledienDist(other: DGps): Float = sqrt(
        (latitude * 1000 - other.latitude * 1000).pow(2) +
                (longitude * 1000 - other.longitude * 1000).pow(2)
    ).let { max(1f, it) }
}