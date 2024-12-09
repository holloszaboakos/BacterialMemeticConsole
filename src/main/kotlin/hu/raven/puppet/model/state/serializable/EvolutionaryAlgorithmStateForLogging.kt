package hu.raven.puppet.model.state.serializable

import hu.raven.puppet.model.solution.SolutionWithIteration

open class EvolutionaryAlgorithmStateForLogging<R>(
    val population: List<IndexedValue<SolutionWithIteration<R>>>,
    val iteration: Int,
)