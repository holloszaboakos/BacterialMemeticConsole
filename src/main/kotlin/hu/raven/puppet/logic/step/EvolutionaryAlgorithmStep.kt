package hu.raven.puppet.logic.step

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

interface EvolutionaryAlgorithmStep<R, in S : EvolutionaryAlgorithmState<R>> {
    operator fun invoke(state: S)
}