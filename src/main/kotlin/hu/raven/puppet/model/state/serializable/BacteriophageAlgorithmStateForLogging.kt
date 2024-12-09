package hu.raven.puppet.model.state.serializable

import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.solution.partial.BacteriophageSpecimen

class BacteriophageAlgorithmStateForLogging<R>(
    population: List<IndexedValue<SolutionWithIteration<R>>>,
    val virusPopulation: List<BacteriophageSpecimen>,
    iteration: Int,
) : EvolutionaryAlgorithmStateForLogging<R>(
    population,
    iteration
)