package hu.raven.puppet.model.task

import hu.raven.puppet.model.physics.Gram
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.physics.CubicMeter

data class CostGraphVertex(
    val location: Gps = Gps(),
    val time: Second = Second(0f),
    val volume: CubicMeter = CubicMeter(0f),
    val weight: Gram = Gram(0f)
)

