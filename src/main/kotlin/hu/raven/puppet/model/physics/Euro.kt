package hu.raven.puppet.model.physics



@JvmInline
value class Euro(override val value: Float) : PhysicsUnit<Euro> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first/second.toFloat())

    override operator fun plus(other: Euro) = Euro(value + other.value)
    override operator fun minus(other: Euro) = Euro(value - other.value)
    override operator fun times(other: Long) = Euro(value * other)
    override operator fun div(other: Long) = Euro(value / other)
    override operator fun compareTo(other: Euro) = value.compareTo(other.value)
}