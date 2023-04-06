package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.logic.step.IterativeAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializeLocalSearch<C : PhysicsUnit<C>> : IterativeAlgorithmStep<C>() {
    abstract operator fun invoke()
}