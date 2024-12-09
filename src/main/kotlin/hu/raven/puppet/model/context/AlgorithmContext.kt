package hu.raven.puppet.model.context

import hu.raven.puppet.model.configuration.AlgorithmConfiguration
import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask

data class AlgorithmContext<T : AlgorithmTask, S : AlgorithmState, C : AlgorithmConfiguration>(
    val task: T,
    val state: S,
    val context: C,
)