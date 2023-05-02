package hu.raven.puppet.model.operator.calculatecost

import hu.raven.puppet.model.physics.Gramm
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.physics.Stere

data class TakenCapacity(
    val volume: Stere,
    val weight: Gramm,
    val time: Second,
)