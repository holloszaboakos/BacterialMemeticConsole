package hu.raven.puppet.model.utility.math

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

data class CompleteGraphWithCenterVertex<C, V, E>(
    val centerVertex: C,
    val vertices: ImmutableArray<GraphVertex<V>>,
    val edgesToCenter:ImmutableArray<GraphEdge<E>>,
    val edgesFromCenter:ImmutableArray<GraphEdge<E>>,
    val edgesBetween: ImmutableArray<ImmutableArray<GraphEdge<E>>>,
)