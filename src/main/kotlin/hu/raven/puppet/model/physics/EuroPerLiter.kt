package hu.raven.puppet.model.physics

import hu.raven.puppet.model.physics.math.Fraction

@JvmInline
value class EuroPerLiter(override val value: Fraction) : PhysicsUnit<EuroPerLiter> {
    constructor(value: Long) : this(Fraction(value))

    override operator fun plus(other: EuroPerLiter) = EuroPerLiter(value + other.value)
    override operator fun times(other: EuroPerLiter) = EuroPerLiter(value * other.value)
    override operator fun times(other: Long) = EuroPerLiter(value * other)
    override operator fun div(other: EuroPerLiter) = EuroPerLiter(value / other.value)
    override operator fun compareTo(other: EuroPerLiter) = value.compareTo(other.value)
    override operator fun div(other: Long) = EuroPerLiter(value / other)
    operator fun times(other: LiterPerMeter) = EuroPerMeter(value * other.value)
}