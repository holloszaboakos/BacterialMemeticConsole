package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray
import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.immutableArrayOf


data class CostGraph(
    val center: Gps = Gps(),
    val objectives: ImmutableArray<CostGraphVertex> = immutableArrayOf(),
    val edgesBetween: ImmutableArray<ImmutableArray<CostGraphEdge>> = immutableArrayOf(),
    val edgesFromCenter: ImmutableArray<CostGraphEdge> = immutableArrayOf(),
    val edgesToCenter: ImmutableArray<CostGraphEdge> = immutableArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CostGraph

        if (center != other.center) return false
        if (!objectives.contentEquals(other.objectives)) return false
        if (!edgesBetween.contentEquals(other.edgesBetween)) return false
        if (!edgesFromCenter.contentEquals(other.edgesFromCenter)) return false
        if (!edgesToCenter.contentEquals(other.edgesToCenter)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = 31 * result + objectives.contentHashCode()
        result = 31 * result + edgesBetween.contentHashCode()
        result = 31 * result + edgesFromCenter.contentHashCode()
        result = 31 * result + edgesToCenter.contentHashCode()
        return result
    }
}