package hu.raven.puppet.model.dataset

import hu.akos.hollo.szabo.Gps


data class LocationWithVolumeAndName(
    val location: Gps,
    val volume: Int,
    val name: String
)