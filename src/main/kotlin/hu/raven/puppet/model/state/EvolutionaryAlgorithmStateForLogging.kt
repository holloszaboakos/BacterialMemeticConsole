package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration

open class EvolutionaryAlgorithmStateForLogging(
    val population: List<IndexedValue<OnePartRepresentationWithCostAndIteration>>,
    val iteration: Int,
)