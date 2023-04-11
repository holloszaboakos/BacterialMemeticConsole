package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.task.Task

class LocalSearchAlgorithmState<C : PhysicsUnit<C>>(
    override val task: Task
) : IterativeAlgorithmState {
    lateinit var actualCandidate: OnePartRepresentationWithIteration<C>
    override var iteration = 0
}