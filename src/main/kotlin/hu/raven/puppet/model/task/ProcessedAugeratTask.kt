package hu.raven.puppet.model.task

import hu.raven.puppet.model.utility.math.CompleteGraph

data class ProcessedAugeratTask(
    val graph: CompleteGraph<LocationWithVolume, Float>,
    val capacity: Int
)