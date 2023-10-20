package hu.raven.puppet.model.physics


@JvmInline
value class Meter(override val value: Float) : PhysicsUnit<Meter> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first / second.toFloat())

    override operator fun plus(other: Meter) = Meter(value + other.value)
    override operator fun minus(other: Meter) = Meter(value - other.value)
    override operator fun times(other: Long) = Meter(value * other)
    override operator fun div(other: Long) = Meter(value / other)
    override operator fun compareTo(other: Meter) = value.compareTo(other.value)
    operator fun div(other: MeterPerSecond) = Second(value / other.value)
}