package hu.raven.puppet.model.state

import hu.raven.puppet.model.solution.AlgorithmSolution

data class LocalSearchState<R, S : AlgorithmSolution<R, S>>(
    override var iteration: Int,
    val candidateSolution: S,
) : IterativeAlgorithmState
