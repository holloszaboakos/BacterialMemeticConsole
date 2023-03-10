package hu.raven.puppet.logic.step.initializePopulation

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class InitializePopulation<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {
    abstract operator fun invoke()
}