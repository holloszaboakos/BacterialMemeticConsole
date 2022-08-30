package hu.raven.puppet.model

import java.util.*

data class DEdgeArray(
    var id: String = UUID.randomUUID().toString(),
    var orderInOwner: Int = 0,
    var values: Array<DEdge> = arrayOf()
) {
    init {
        values.forEachIndexed { index, gps -> gps.orderInOwner = index }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DEdgeArray

        if (id != other.id) return false
        if (orderInOwner != other.orderInOwner) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + orderInOwner
        result = 31 * result + values.contentHashCode()
        return result
    }
}
