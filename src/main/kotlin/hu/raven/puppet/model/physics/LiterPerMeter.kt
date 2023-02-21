package hu.raven.puppet.model.physics

import hu.raven.puppet.model.physics.math.Fraction

@JvmInline
value class LiterPerMeter(override val value: Fraction) : PhysicsUnit<LiterPerMeter> {
    constructor(value: Long) : this(Fraction(value))

    override operator fun plus(other: LiterPerMeter) = LiterPerMeter(value + other.value)
    override operator fun times(other: LiterPerMeter) = LiterPerMeter(value * other.value)
    override operator fun times(other: Long) = LiterPerMeter(value * other)
    override operator fun div(other: LiterPerMeter) = LiterPerMeter(value / other.value)
    override operator fun div(other: Long) = LiterPerMeter(value / other)
    override operator fun compareTo(other: LiterPerMeter) = value.compareTo(other.value)
}