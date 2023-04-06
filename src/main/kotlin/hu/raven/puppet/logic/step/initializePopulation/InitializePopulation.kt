package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializePopulation<C : PhysicsUnit<C>> {
    abstract operator fun invoke()
}