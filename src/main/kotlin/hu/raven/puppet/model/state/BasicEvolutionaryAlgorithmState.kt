package hu.raven.puppet.model.state

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.model.solution.SolutionWithIteration

data class BasicEvolutionaryAlgorithmState<R>(
    override var iteration: Int,
    override val population: PoolWithActivation<SolutionWithIteration<R>>,
    override var copyOfBest: IndexedValue<SolutionWithIteration<R>>?,
    override var copyOfWorst: IndexedValue<SolutionWithIteration<R>>?
) : EvolutionaryAlgorithmState<R>
