package hu.raven.puppet.model.state

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration


open class EvolutionaryAlgorithmState<T>(
    override val task: T,
    open val population: PoolWithActivation<OnePartRepresentationWithCostAndIteration>
) : IterativeAlgorithmState<T> {
    override var iteration = 0
    var copyOfBest: IndexedValue<OnePartRepresentationWithCostAndIteration>? = null
    var copyOfWorst: IndexedValue<OnePartRepresentationWithCostAndIteration>? = null
}