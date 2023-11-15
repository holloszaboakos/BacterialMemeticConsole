package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.task.Task

open class EvolutionaryAlgorithmState(
    override val task: Task,
    val population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId>
) : IterativeAlgorithmState {
    override var iteration = 0
    var copyOfBest: OnePartRepresentationWithCostAndIterationAndId? = null
    var copyOfWorst: OnePartRepresentationWithCostAndIterationAndId? = null
}