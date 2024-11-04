package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration

class BacteriophageAlgorithmStateForLogging(
    population: List<IndexedValue<OnePartRepresentationWithCostAndIteration>>,
    val virusPopulation: List<BacteriophageSpecimen>,
    iteration: Int,
) : EvolutionaryAlgorithmStateForLogging(
    population,
    iteration
)