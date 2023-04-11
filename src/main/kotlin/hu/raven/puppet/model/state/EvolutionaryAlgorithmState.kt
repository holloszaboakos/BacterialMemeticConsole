package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.task.Task

class EvolutionaryAlgorithmState<C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    override var iteration = 0
    var population: PoolWithSmartActivation<OnePartRepresentationWithIteration<C>> =
        PoolWithSmartActivation(mutableListOf())
    var copyOfBest: PoolItem<OnePartRepresentationWithIteration<C>>? = null
    var copyOfWorst: PoolItem<OnePartRepresentationWithIteration<C>>? = null
}