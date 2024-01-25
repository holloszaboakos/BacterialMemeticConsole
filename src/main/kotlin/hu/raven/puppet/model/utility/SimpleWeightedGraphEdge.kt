package hu.raven.puppet.model.utility

data class SimpleWeightedGraphEdge(
    val sourceNodeIndex: Int,
    val targetNodeIndex: Int,
    val weight: Int
)
