package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.task.Task

class IterativeAlgorithmStateWithSingleCandidate<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    lateinit var actualCandidate: S
    override var iteration = 0
}