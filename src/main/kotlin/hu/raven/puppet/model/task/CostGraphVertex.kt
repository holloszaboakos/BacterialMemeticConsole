package hu.raven.puppet.model.task

import hu.akos.hollo.szabo.physics.CubicMeter
import hu.akos.hollo.szabo.physics.Gram
import hu.akos.hollo.szabo.physics.Second

data class CostGraphVertex(
    val location: Gps = Gps(),
    val time: Second = Second(0f),
    val volume: CubicMeter = CubicMeter(0f),
    val weight: Gram = Gram(0f)
)

