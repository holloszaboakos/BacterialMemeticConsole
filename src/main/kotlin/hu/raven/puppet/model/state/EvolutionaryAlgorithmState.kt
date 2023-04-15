package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.task.Task

class EvolutionaryAlgorithmState<C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    override var iteration = 0
    var population: PoolWithSmartActivation<OnePartRepresentationWithCostAndIterationAndId<C>> =
        PoolWithSmartActivation(mutableListOf())
    var copyOfBest: OnePartRepresentationWithCostAndIterationAndId<C>? = null
    var copyOfWorst: OnePartRepresentationWithCostAndIterationAndId<C>? = null
}