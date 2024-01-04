package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.contentEquals
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.contentHashCode
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.immutableArrayOf


data class Task(
    val transportUnits: ImmutableArray<TransportUnit> = immutableArrayOf(),
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

