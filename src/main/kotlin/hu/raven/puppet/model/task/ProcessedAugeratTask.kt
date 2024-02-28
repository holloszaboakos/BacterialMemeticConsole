package hu.raven.puppet.model.task

import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.CompleteGraphWithCenterVertex

data class ProcessedAugeratTask(
    val graph: CompleteGraphWithCenterVertex<Gps, LocationWithVolume, Float>,
    val capacity: Int
)