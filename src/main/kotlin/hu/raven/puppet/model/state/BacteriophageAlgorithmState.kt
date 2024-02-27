package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation

class BacteriophageAlgorithmState<T>(
    task: T,
    population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>,
    val virusPopulation: PoolWithSmartActivation<BacteriophageSpecimen>,
) : EvolutionaryAlgorithmState<T>(task, population)