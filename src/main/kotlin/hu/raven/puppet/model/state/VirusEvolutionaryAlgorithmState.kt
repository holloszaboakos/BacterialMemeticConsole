package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.solution.VirusSpecimen

data class VirusEvolutionaryAlgorithmState<T>(
    override val task: T,
    override val population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>,
    val virusPopulation: PoolWithSmartActivation<VirusSpecimen>,
) : EvolutionaryAlgorithmState<T>(task, population)