package hu.raven.puppet.model.state

sealed interface IterativeAlgorithmState : AlgorithmState {
    var iteration: Int
}