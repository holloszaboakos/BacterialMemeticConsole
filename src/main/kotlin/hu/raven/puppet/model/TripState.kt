package hu.raven.puppet.model

import hu.raven.puppet.model.physics.Euro

data class TripState(
    val takenCapacity: TakenCapacity,
    val cost: Euro
)