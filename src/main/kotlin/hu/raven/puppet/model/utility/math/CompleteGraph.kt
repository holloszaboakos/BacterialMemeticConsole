package hu.raven.puppet.model.utility.math

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

data class CompleteGraph<V, E>(
    val vertices: ImmutableArray<V>,
    val edges: ImmutableArray<ImmutableArray<E>>,
)