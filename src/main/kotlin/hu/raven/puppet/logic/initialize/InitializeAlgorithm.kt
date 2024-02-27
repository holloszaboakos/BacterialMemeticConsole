package hu.raven.puppet.logic.initialize


import hu.raven.puppet.model.state.AlgorithmState

sealed interface InitializeAlgorithm<T, A : AlgorithmState<T>> {
    operator fun invoke(task: T): A
}