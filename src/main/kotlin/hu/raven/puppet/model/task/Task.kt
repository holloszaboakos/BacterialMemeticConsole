package hu.raven.puppet.model.task

import hu.raven.puppet.utility.ImmutableArray
import hu.raven.puppet.utility.ImmutableArray.Companion.immutableArrayOf

data class Task(
    val transportUnits: ImmutableArray<out TransportUnit> = immutableArrayOf(),
    val costGraph: CostGraph = CostGraph()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (!transportUnits.contentEquals(other.transportUnits)) return false
        if (costGraph != other.costGraph) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transportUnits.contentHashCode()
        result = 31 * result + costGraph.hashCode()
        return result
    }
}

