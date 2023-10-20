package hu.raven.puppet.model.physics



@JvmInline
value class EuroPerSecond(override val value: Float) : PhysicsUnit<EuroPerSecond> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first/second.toFloat())

    override operator fun plus(other: EuroPerSecond) = EuroPerSecond(value + other.value)
    override operator fun minus(other: EuroPerSecond) = EuroPerSecond(value - other.value)
    override operator fun times(other: Long) = EuroPerSecond(value * other)
    override operator fun div(other: Long) = EuroPerSecond(value / other)
    override operator fun compareTo(other: EuroPerSecond) = value.compareTo(other.value)
    operator fun times(other: Second) = Euro(value * other.value)
}