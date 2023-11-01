package hu.raven.puppet.logic.initialize


import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.task.Task

sealed interface InitializeAlgorithm<A : AlgorithmState> {
    operator fun invoke(task: Task): A
}