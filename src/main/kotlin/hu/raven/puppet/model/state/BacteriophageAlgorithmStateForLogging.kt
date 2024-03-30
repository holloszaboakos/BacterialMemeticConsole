package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

class BacteriophageAlgorithmStateForLogging(
    population: List<OnePartRepresentationWithCostAndIterationAndId>,
    val virusPopulation: List<BacteriophageSpecimen>,
    iteration: Int,
) : EvolutionaryAlgorithmStateForLogging(
    population,
    iteration
)