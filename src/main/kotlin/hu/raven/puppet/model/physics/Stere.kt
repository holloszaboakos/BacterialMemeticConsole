package hu.raven.puppet.model.physics

import hu.raven.puppet.model.math.Fraction

@JvmInline
value class Stere(override val value: Fraction) : PhysicsUnit<Stere> {
    constructor(value: Long) : this(Fraction.new(value))
    constructor(first: Long, second: Long) : this(Fraction.new(first, second))

    override operator fun plus(other: Stere) = Stere(value + other.value)
    override operator fun minus(other: Stere) = Stere(value - other.value)
    override operator fun times(other: Long) = Stere(value * other)
    override operator fun div(other: Long) = Stere(value / other)
    override operator fun compareTo(other: Stere) = value.compareTo(other.value)
}