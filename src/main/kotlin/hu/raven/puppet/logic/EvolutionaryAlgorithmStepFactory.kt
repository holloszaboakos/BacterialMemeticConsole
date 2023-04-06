package hu.raven.puppet.logic

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

interface EvolutionaryAlgorithmStepFactory<C : PhysicsUnit<C>> {
    operator fun invoke(): EvolutionaryAlgorithmState<C>.() -> Unit
}