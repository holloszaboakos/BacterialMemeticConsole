package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.state.IterativeAlgorithmState

sealed class IterativeAlgorithmParameterProvider (
    protected open val algorithmState: IterativeAlgorithmState,
    val iterationLimit: Int,
)