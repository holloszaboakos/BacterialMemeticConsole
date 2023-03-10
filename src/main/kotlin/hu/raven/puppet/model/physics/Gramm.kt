package hu.raven.puppet.model.physics

import hu.raven.puppet.model.math.Fraction

@JvmInline
value class Gramm(override val value: Fraction) : PhysicsUnit<Gramm> {
    constructor(value: Long) : this(Fraction.new(value))
    constructor(first: Long, second: Long) : this(Fraction.new(first, second))

    override operator fun plus(other: Gramm) = Gramm(value + other.value)
    override operator fun minus(other: Gramm) = Gramm(value - other.value)
    override operator fun times(other: Long) = Gramm(value * other)
    override operator fun div(other: Long) = Gramm(value / other)
    override operator fun compareTo(other: Gramm) = value.compareTo(other.value)
}