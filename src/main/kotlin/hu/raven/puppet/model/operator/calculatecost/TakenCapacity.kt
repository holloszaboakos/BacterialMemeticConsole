package hu.raven.puppet.model.operator.calculatecost

import hu.akos.hollo.szabo.physics.CubicMeter
import hu.akos.hollo.szabo.physics.Gram
import hu.akos.hollo.szabo.physics.Second

data class TakenCapacity(
    val volume: CubicMeter,
    val weight: Gram,
    val time: Second,
)