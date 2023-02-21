package hu.raven.puppet.model.physics

import hu.raven.puppet.model.physics.math.Fraction

@JvmInline
value class MeterPerSecond(override val value: Fraction) : PhysicsUnit<MeterPerSecond> {
    constructor(value: Long) : this(Fraction(value))

    override operator fun plus(other: MeterPerSecond) = MeterPerSecond(value + other.value)
    override operator fun minus(other: MeterPerSecond) = MeterPerSecond(value - other.value)
    override operator fun times(other: Long) = MeterPerSecond(value * other)
    override operator fun div(other: Long) = MeterPerSecond(value / other)
    override operator fun compareTo(other: MeterPerSecond) = value.compareTo(other.value)
}