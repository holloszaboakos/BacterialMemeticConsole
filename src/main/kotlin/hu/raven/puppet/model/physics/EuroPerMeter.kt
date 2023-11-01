package hu.raven.puppet.model.physics


@JvmInline
value class EuroPerMeter(override val value: Float) : PhysicsUnit<EuroPerMeter> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first / second.toFloat())

    override operator fun plus(other: EuroPerMeter) = EuroPerMeter(value + other.value)
    override operator fun minus(other: EuroPerMeter) = EuroPerMeter(value - other.value)
    override operator fun times(other: Long) = EuroPerMeter(value * other)
    override operator fun div(other: Long) = EuroPerMeter(value / other)
    override operator fun compareTo(other: EuroPerMeter) = value.compareTo(other.value)
    operator fun times(other: Meter) = Euro(value * other.value)
}