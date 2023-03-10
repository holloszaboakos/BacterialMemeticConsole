package hu.raven.puppet.model.physics

import hu.raven.puppet.model.math.Fraction

sealed interface PhysicsUnit<S : PhysicsUnit<S>> {
    val value: Fraction
    operator fun plus(other: S): S
    operator fun minus(other: S): S
    operator fun times(other: Long): S
    operator fun div(other: Long): S
    operator fun compareTo(other: S): Int
}