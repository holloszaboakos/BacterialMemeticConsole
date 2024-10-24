package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.MutableCompleteGraph

inline fun <reified V, reified E> CompleteGraph<V, E>.toMutable() = MutableCompleteGraph(
    edges = edges.asSequence().map { it.asList().toTypedArray() }.toList().toTypedArray(),
    vertices = vertices.asList().toTypedArray()
)