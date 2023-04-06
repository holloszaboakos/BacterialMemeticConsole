package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializeLocalSearch<C : PhysicsUnit<C>> {
    abstract operator fun invoke()
}