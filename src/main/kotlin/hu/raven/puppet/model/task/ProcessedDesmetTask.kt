package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.physics.Second
import hu.raven.puppet.model.utility.Gps
import hu.raven.puppet.model.utility.math.CompleteGraph

data class ProcessedDesmetTask(
    val graph: CompleteGraph<LocationWithVolumeAndName, Second>,
    val capacity: Int
)