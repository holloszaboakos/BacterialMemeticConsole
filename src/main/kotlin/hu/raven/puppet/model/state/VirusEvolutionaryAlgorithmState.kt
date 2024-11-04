package hu.raven.puppet.model.state

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

import hu.raven.puppet.model.solution.VirusSpecimen

data class VirusEvolutionaryAlgorithmState<T>(
    override val task: T,
    override val population: PoolWithActivation<OnePartRepresentationWithCostAndIteration>,
    val virusPopulation: PoolWithActivation<VirusSpecimen>,
) : EvolutionaryAlgorithmState<T>(task, population)