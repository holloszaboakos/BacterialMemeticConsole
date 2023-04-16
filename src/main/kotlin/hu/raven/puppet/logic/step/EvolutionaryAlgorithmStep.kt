package hu.raven.puppet.logic.step

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

interface EvolutionaryAlgorithmStep<C : PhysicsUnit<C>> {
    operator fun invoke(state: EvolutionaryAlgorithmState<C>)
}