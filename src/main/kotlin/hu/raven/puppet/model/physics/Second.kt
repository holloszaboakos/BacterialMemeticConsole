package hu.raven.puppet.model.physics

import hu.raven.puppet.model.physics.math.Fraction

@JvmInline
value class Second(override val value: Fraction) : PhysicsUnit<Second> {
    constructor(value: Long) : this(Fraction(value))

    override operator fun plus(other: Second) = Second(value + other.value)
    override operator fun minus(other: Second) = Second(value - other.value)
    override operator fun times(other: Long) = Second(value * other)
    override operator fun div(other: Long) = Second(value / other)
    override operator fun compareTo(other: Second) = value.compareTo(other.value)
}