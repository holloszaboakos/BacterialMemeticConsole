package hu.raven.puppet.model.physics

import hu.raven.puppet.model.math.Fraction

@JvmInline
value class EuroPerMeter(override val value: Fraction) : PhysicsUnit<EuroPerMeter> {
    constructor(value: Long) : this(Fraction.new(value))
    constructor(first: Long, second: Long) : this(Fraction.new(first, second))

    override operator fun plus(other: EuroPerMeter) = EuroPerMeter(value + other.value)
    override operator fun minus(other: EuroPerMeter) = EuroPerMeter(value - other.value)
    override operator fun times(other: Long) = EuroPerMeter(value * other)
    override operator fun div(other: Long) = EuroPerMeter(value / other)
    override operator fun compareTo(other: EuroPerMeter) = value.compareTo(other.value)
    operator fun times(other: Meter) = Euro(value * other.value)
}