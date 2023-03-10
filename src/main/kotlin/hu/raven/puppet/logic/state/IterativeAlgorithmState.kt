package hu.raven.puppet.logic.state

sealed interface IterativeAlgorithmState : AlgorithmState {
    var iteration: Int
}