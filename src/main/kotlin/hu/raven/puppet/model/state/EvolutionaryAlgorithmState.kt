package hu.raven.puppet.model.state

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.model.solution.SolutionWithIteration

sealed interface EvolutionaryAlgorithmState<R> : IterativeAlgorithmState {
    override var iteration: Int
    val population: PoolWithActivation<SolutionWithIteration<R>>
    var copyOfBest: IndexedValue<SolutionWithIteration<R>>?
    var copyOfWorst: IndexedValue<SolutionWithIteration<R>>?
}
