package hu.raven.puppet.model.operator.calculatecost

import hu.raven.puppet.model.physics.CubicMeter
import hu.raven.puppet.model.physics.Gram
import hu.raven.puppet.model.physics.Second

data class TakenCapacity(
    val volume: CubicMeter,
    val weight: Gram,
    val time: Second,
)