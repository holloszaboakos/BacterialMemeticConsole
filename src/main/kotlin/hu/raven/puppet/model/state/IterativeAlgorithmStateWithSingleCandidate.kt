package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

class IterativeAlgorithmStateWithSingleCandidate<C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    lateinit var actualCandidate: OnePartRepresentation<C>
    override var iteration = 0
}