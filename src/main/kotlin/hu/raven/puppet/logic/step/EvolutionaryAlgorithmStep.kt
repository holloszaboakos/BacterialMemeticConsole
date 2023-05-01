package hu.raven.puppet.logic.step

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

interface EvolutionaryAlgorithmStep {
    operator fun invoke(state: EvolutionaryAlgorithmState)
}