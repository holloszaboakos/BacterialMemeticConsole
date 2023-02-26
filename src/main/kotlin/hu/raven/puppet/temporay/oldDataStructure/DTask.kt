package hu.raven.puppet.temporay.oldDataStructure

import java.util.*

data class DTask(
    var id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val salesmen: Array<DSalesman> = arrayOf(),
    val costGraph: DGraph = DGraph()
) {
    init {
        salesmen.forEachIndexed { index, value ->
            value.orderInOwner = index
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DTask

        if (id != other.id) return false
        if (name != other.name) return false
        if (!salesmen.contentEquals(other.salesmen)) return false
        if (costGraph != other.costGraph) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + salesmen.contentHashCode()
        result = 31 * result + costGraph.hashCode()
        return result
    }
}