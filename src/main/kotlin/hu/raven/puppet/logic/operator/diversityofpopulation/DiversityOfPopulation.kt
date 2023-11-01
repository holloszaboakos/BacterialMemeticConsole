package hu.raven.puppet.logic.operator.diversityofpopulation

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed interface DiversityOfPopulation {
    operator fun invoke(algorithmState: EvolutionaryAlgorithmState): Double
}