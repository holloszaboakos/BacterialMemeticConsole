package hu.raven.puppet.model.state

sealed interface IterativeAlgorithmState<T> : AlgorithmState<T> {
    var iteration: Int
}