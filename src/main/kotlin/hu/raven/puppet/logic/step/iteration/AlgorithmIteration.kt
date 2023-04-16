package hu.raven.puppet.logic.step.iteration

import hu.raven.puppet.model.state.AlgorithmState

interface AlgorithmIteration<A : AlgorithmState> {
    operator fun invoke(algorithmState: A)
}