package hu.raven.puppet.logic.operator.diversity

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class Diversity {
    abstract operator fun invoke(algorithmState: EvolutionaryAlgorithmState): Double
}