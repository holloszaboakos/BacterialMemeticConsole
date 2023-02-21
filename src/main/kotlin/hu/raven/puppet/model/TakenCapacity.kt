package hu.raven.puppet.model

import hu.raven.puppet.model.physics.Gramm
import hu.raven.puppet.model.physics.Second
import hu.raven.puppet.model.physics.Stere

data class TakenCapacity(
    val volume: Stere = Stere(0L),
    val weight: Gramm = Gramm(0L),
    val time: Second = Second(0L),
)