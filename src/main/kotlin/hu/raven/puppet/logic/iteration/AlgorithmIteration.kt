package hu.raven.puppet.logic.iteration

import hu.raven.puppet.model.state.AlgorithmState

sealed interface AlgorithmIteration<S : AlgorithmState> {
    operator fun invoke(algorithmState: S)
}