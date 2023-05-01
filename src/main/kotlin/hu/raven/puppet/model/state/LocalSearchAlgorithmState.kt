package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.task.Task

data class LocalSearchAlgorithmState(
    override val task: Task
) : IterativeAlgorithmState {
    lateinit var actualCandidate: OnePartRepresentationWithCostAndIteration
    override var iteration = 0
}