package hu.raven.puppet.model.physics

@JvmInline
value class MeterPerSecond(override val value: Float) : PhysicsUnit<MeterPerSecond> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first / second.toFloat())

    override operator fun plus(other: MeterPerSecond) = MeterPerSecond(value + other.value)
    override operator fun minus(other: MeterPerSecond) = MeterPerSecond(value - other.value)
    override operator fun times(other: Long) = MeterPerSecond(value * other)
    override operator fun div(other: Long) = MeterPerSecond(value / other)
    override operator fun compareTo(other: MeterPerSecond) = value.compareTo(other.value)
}