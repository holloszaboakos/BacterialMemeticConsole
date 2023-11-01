package hu.raven.puppet.logic.iteration

import hu.raven.puppet.model.state.AlgorithmState

sealed interface AlgorithmIteration<A : AlgorithmState> {
    operator fun invoke(algorithmState: A)
}