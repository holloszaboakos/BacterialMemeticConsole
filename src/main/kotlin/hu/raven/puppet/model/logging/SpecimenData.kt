package hu.raven.puppet.model.logging

import hu.raven.puppet.model.physics.PhysicsUnit

data class SpecimenData<C : PhysicsUnit<C>>(
    val id: Int,
    val cost: C
)