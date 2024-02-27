package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation

open class EvolutionaryAlgorithmState<T>(
    override val task: T,
    open val population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>
) : IterativeAlgorithmState<T> {
    override var iteration = 0
    var copyOfBest: OnePartRepresentationWithCostAndIterationAndId? = null
    var copyOfWorst: OnePartRepresentationWithCostAndIterationAndId? = null
}