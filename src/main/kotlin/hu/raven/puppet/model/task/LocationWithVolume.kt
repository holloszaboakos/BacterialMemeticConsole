package hu.raven.puppet.model.task

import hu.raven.puppet.model.utility.Gps

data class LocationWithVolume(
    val location: Gps,
    val volume: Int
)