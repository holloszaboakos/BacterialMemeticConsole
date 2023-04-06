package hu.raven.puppet.logic.step.initialize


import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializeAlgorithm<C : PhysicsUnit<C>> {
    abstract operator fun invoke()
}