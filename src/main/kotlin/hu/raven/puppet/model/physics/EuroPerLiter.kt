package hu.raven.puppet.model.physics



@JvmInline
value class EuroPerLiter(override val value: Float) : PhysicsUnit<EuroPerLiter> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first/second.toFloat())

    override operator fun plus(other: EuroPerLiter) = EuroPerLiter(value + other.value)
    override operator fun minus(other: EuroPerLiter) = EuroPerLiter(value - other.value)
    override operator fun times(other: Long) = EuroPerLiter(value * other)
    override operator fun compareTo(other: EuroPerLiter) = value.compareTo(other.value)
    override operator fun div(other: Long) = EuroPerLiter(value / other)
    operator fun times(other: LiterPerMeter) = EuroPerMeter(value * other.value)
}