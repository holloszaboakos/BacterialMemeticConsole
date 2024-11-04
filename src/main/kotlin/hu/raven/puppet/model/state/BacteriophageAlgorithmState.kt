package hu.raven.puppet.model.state

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.model.solution.BacteriophageSpecimen
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration


class BacteriophageAlgorithmState<T>(
    task: T,
    population: PoolWithActivation<OnePartRepresentationWithCostAndIteration>,
    val virusPopulation: PoolWithActivation<BacteriophageSpecimen>,
) : EvolutionaryAlgorithmState<T>(task, population)