package hu.raven.puppet.model.task

import hu.raven.puppet.model.utility.Gps

data class LocationWithVolumeAndName(
    val location: Gps,
    val volume: Int,
    val name: String
)