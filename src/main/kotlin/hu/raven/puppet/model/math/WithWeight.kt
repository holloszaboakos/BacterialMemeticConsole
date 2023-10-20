package hu.raven.puppet.model.math

data class WithWeight<T>(
    val weight: Fraction,
    val element: T
)
