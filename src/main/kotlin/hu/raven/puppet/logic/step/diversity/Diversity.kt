package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class Diversity<C : PhysicsUnit<C>> {
    abstract operator fun invoke(algorithmState: EvolutionaryAlgorithmState<C>): Double
}