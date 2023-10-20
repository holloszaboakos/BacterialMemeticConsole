package hu.raven.puppet.model.math

data class WithWeight<T>(
    val weight: Float,
    val element: T
)
