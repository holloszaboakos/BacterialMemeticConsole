package hu.raven.puppet.model.operator.calculatecost

import hu.raven.puppet.model.physics.Gram
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.physics.CubicMeter

data class TakenCapacity(
    val volume: CubicMeter,
    val weight: Gram,
    val time: Second,
)