package hu.raven.puppet.model.dataset.augerat.desmet

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

@JvmInline
value class DistanceMatrix(val distances: ImmutableArray<DoubleArray>)