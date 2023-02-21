package hu.raven.puppet.model.physics

import hu.raven.puppet.model.physics.math.Fraction

@JvmInline
value class Meter(override val value: Fraction) : PhysicsUnit<Meter> {
    constructor(value: Long) : this(Fraction(value))

    override operator fun plus(other: Meter) = Meter(value + other.value)
    override operator fun times(other: Meter) = Meter(value * other.value)
    override operator fun times(other: Long) = Meter(value * other)
    override operator fun div(other: Meter) = Meter(value / other.value)
    override operator fun div(other: Long) = Meter(value / other)
    override operator fun compareTo(other: Meter) = value.compareTo(other.value)
    operator fun div(other: MeterPerSecond) = Second(value / other.value)
}