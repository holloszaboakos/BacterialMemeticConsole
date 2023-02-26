package hu.raven.puppet.model.physics

import hu.raven.puppet.model.physics.math.Fraction

@JvmInline
value class EuroPerLiter(override val value: Fraction) : PhysicsUnit<EuroPerLiter> {
    constructor(value: Long) : this(Fraction(value))
    constructor(first: Long, second: Long) : this(Fraction(first, second))

    override operator fun plus(other: EuroPerLiter) = EuroPerLiter(value + other.value)
    override operator fun minus(other: EuroPerLiter) = EuroPerLiter(value - other.value)
    override operator fun times(other: Long) = EuroPerLiter(value * other)
    override operator fun compareTo(other: EuroPerLiter) = value.compareTo(other.value)
    override operator fun div(other: Long) = EuroPerLiter(value / other)
    operator fun times(other: LiterPerMeter) = EuroPerMeter(value * other.value)
}