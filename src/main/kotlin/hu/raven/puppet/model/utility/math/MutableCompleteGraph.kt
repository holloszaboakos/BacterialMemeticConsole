package hu.raven.puppet.model.utility.math

data class MutableCompleteGraph<E, V>(
    val vertices: Array<GraphVertex<V>>,
    val edges: Array<Array<GraphEdge<E>>>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MutableCompleteGraph<*, *>

        if (!vertices.contentEquals(other.vertices)) return false
        if (!edges.contentDeepEquals(other.edges)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + edges.contentDeepHashCode()
        return result
    }
}