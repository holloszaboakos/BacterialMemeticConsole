package hu.raven.puppet.model.utility.math

data class CompleteGraphEdge<E>(
    val fromIndex: Int,
    val toIndex: Int,
    val value: E
)