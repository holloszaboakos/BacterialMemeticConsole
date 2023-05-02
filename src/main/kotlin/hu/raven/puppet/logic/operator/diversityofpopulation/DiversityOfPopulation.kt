package hu.raven.puppet.logic.operator.diversityofpopulation

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class DiversityOfPopulation {
    abstract operator fun invoke(algorithmState: EvolutionaryAlgorithmState): Double
}