package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration

data class LocalSearchAlgorithmState<T>(
    override val task: T
) : IterativeAlgorithmState<T> {
    lateinit var actualCandidate: OnePartRepresentationWithCostAndIteration
    override var iteration = 0
}