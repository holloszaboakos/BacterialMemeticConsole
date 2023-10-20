package hu.raven.puppet.model.physics

@JvmInline
value class CubicMeter(override val value: Float) : PhysicsUnit<CubicMeter> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Long, second: Long) : this(first / second.toFloat())

    override operator fun plus(other: CubicMeter) = CubicMeter(value + other.value)
    override operator fun minus(other: CubicMeter) = CubicMeter(value - other.value)
    override operator fun times(other: Long) = CubicMeter(value * other)
    override operator fun div(other: Long) = CubicMeter(value / other)
    override operator fun compareTo(other: CubicMeter) = value.compareTo(other.value)
}