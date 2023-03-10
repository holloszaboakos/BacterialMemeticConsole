package hu.raven.puppet.model.physics

import hu.raven.puppet.model.math.Fraction

@JvmInline
value class Euro(override val value: Fraction) : PhysicsUnit<Euro> {
    constructor(value: Long) : this(Fraction.new(value))
    constructor(first: Long, second: Long) : this(Fraction.new(first, second))
    override operator fun plus(other: Euro) = Euro(value + other.value)
    override operator fun minus(other: Euro) = Euro(value - other.value)
    override operator fun times(other: Long) = Euro(value * other)
    override operator fun div(other: Long) = Euro(value / other)
    override operator fun compareTo(other: Euro) = value.compareTo(other.value)
}