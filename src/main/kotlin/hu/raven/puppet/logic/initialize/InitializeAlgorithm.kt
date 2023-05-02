package hu.raven.puppet.logic.initialize


import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.task.Task

sealed class InitializeAlgorithm<A : AlgorithmState> {
    abstract operator fun invoke(task: Task): A
}