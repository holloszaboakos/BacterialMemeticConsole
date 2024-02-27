package hu.raven.puppet.model.state

sealed interface AlgorithmState<T> {
    val task: T
}