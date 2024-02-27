package hu.raven.puppet.model.utility.math

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

data class CompleteGraphWithCenterVertex<C, V, E>(
    val centerVertex: C,
    val vertices: ImmutableArray<CompleteGraphVertex<V>>,
    val edgesToCenter:ImmutableArray<CompleteGraphEdge<E>>,
    val edgesFromCenter:ImmutableArray<CompleteGraphEdge<E>>,
    val edgesBetween: ImmutableArray<ImmutableArray<CompleteGraphEdge<E>>>,
)