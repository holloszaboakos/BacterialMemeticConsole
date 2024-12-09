package hu.raven.puppet.model.state

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.solution.partial.VirusSpecimen

data class VirusAlgorithmState<R>(
    override var iteration: Int,
    override val population: PoolWithActivation<SolutionWithIteration<R>>,
    val virusPopulation: PoolWithActivation<VirusSpecimen>,
    override var copyOfBest: IndexedValue<SolutionWithIteration<R>>?,
    override var copyOfWorst: IndexedValue<SolutionWithIteration<R>>?
) : EvolutionaryAlgorithmState<R>