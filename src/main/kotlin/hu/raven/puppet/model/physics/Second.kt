package hu.raven.puppet.model.physics

@JvmInline
value class Second(override val value: Float) : PhysicsUnit<Second> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first / second.toFloat())

    override operator fun plus(other: Second) = Second(value + other.value)
    override operator fun minus(other: Second) = Second(value - other.value)
    override operator fun times(other: Long) = Second(value * other)
    override operator fun div(other: Long) = Second(value / other)
    override operator fun compareTo(other: Second) = value.compareTo(other.value)
}