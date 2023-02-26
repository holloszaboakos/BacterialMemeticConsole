package hu.raven.puppet.temporay.oldDataStructure

import hu.raven.puppet.model.task.graph.DGps
import java.util.*

data class DEdge(
    var id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var orderInOwner: Int = 0,
    val length_Meter: Long = 0L,
    val route: Array<DGps> = arrayOf()
) {
    init {
        route.forEachIndexed { index, gps -> gps.orderInOwner = index }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DEdge

        if (id != other.id) return false
        if (name != other.name) return false
        if (orderInOwner != other.orderInOwner) return false
        if (length_Meter != other.length_Meter) return false
        if (!route.contentEquals(other.route)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + orderInOwner
        result = 31 * result + length_Meter.hashCode()
        result = 31 * result + route.contentHashCode()
        return result
    }

}