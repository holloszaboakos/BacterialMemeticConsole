package hu.raven.puppet.model.task.desmet

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray

@JvmInline
value class DistanceMatrix(val distances: ImmutableArray<DoubleArray>)