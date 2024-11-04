package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.Gps

data class LocationWithVolume(
    val location: Gps,
    val volume: Int
)