package hu.raven.puppet.logic.step

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

interface EvolutionaryAlgorithmStep<in T : EvolutionaryAlgorithmState<*>> {
    operator fun invoke(state: T)
}