package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializePopulation<C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<C>() {
    abstract operator fun invoke()
}