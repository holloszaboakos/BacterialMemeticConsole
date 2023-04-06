package hu.raven.puppet.model.state

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task

data class LocalSearchAlgorithmState<C : PhysicsUnit<C>>(
    override val task: Task,
    override var iteration: Int,
    val actualCandidate: OnePartRepresentation<C>,
) : IterativeAlgorithmState