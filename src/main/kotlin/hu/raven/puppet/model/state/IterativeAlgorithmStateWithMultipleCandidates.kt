package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

class IterativeAlgorithmStateWithMultipleCandidates<C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    override var iteration = 0
    var population: MutableList<OnePartRepresentation<C>> = mutableListOf()
    var copyOfBest: OnePartRepresentation<C>? = null
    var copyOfWorst: OnePartRepresentation<C>? = null
}