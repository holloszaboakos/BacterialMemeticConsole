package hu.raven.puppet.logic.operator.diversity_of_population

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed interface DiversityOfPopulation {
    operator fun invoke(algorithmState: EvolutionaryAlgorithmState<*>): Double
}