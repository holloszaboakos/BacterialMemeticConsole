package hu.raven.puppet.logic.initialize


import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask

sealed interface InitializeAlgorithm<T : AlgorithmTask, A : AlgorithmState> {
    operator fun invoke(task: T): A
}