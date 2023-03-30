package hu.raven.puppet.model.state

import hu.raven.puppet.model.task.Task

sealed interface AlgorithmState {
    val task: Task
}