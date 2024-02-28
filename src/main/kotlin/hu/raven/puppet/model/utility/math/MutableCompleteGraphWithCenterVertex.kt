package hu.raven.puppet.model.utility.math

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

data class MutableCompleteGraphWithCenterVertex<C, V, E>(
    val centerVertex: C,
    val vertices: Array<GraphVertex<V>>,
    val edgesToCenter: Array<GraphEdge<E>>,
    val edgesFromCenter: Array<GraphEdge<E>>,
    val edgesBetween: Array<ImmutableArray<GraphEdge<E>>>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MutableCompleteGraphWithCenterVertex<*, *, *>

        if (centerVertex != other.centerVertex) return false
        if (!vertices.contentEquals(other.vertices)) return false
        if (!edgesToCenter.contentEquals(other.edgesToCenter)) return false
        if (!edgesFromCenter.contentEquals(other.edgesFromCenter)) return false
        if (!edgesBetween.contentEquals(other.edgesBetween)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = centerVertex?.hashCode() ?: 0
        result = 31 * result + vertices.contentHashCode()
        result = 31 * result + edgesToCenter.contentHashCode()
        result = 31 * result + edgesFromCenter.contentHashCode()
        result = 31 * result + edgesBetween.contentHashCode()
        return result
    }
}