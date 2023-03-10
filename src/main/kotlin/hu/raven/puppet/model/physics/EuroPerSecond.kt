package hu.raven.puppet.model.physics

import hu.raven.puppet.model.math.Fraction

@JvmInline
value class EuroPerSecond(override val value: Fraction) : PhysicsUnit<EuroPerSecond> {
    constructor(value: Long) : this(Fraction.new(value))
    constructor(first: Long, second: Long) : this(Fraction.new(first, second))

    override operator fun plus(other: EuroPerSecond) = EuroPerSecond(value + other.value)
    override operator fun minus(other: EuroPerSecond) = EuroPerSecond(value - other.value)
    override operator fun times(other: Long) = EuroPerSecond(value * other)
    override operator fun div(other: Long) = EuroPerSecond(value / other)
    override operator fun compareTo(other: EuroPerSecond) = value.compareTo(other.value)
    operator fun times(other: Second) = Euro(value * other.value)
}