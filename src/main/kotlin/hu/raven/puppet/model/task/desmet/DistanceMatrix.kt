package hu.raven.puppet.model.task.desmet

import hu.raven.puppet.utility.ImmutableArray

@JvmInline
value class DistanceMatrix(val distances: ImmutableArray<DoubleArray>)