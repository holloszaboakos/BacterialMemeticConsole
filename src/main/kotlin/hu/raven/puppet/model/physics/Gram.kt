package hu.raven.puppet.model.physics



@JvmInline
value class Gram(override val value: Float) : PhysicsUnit<Gram> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first/second.toFloat())

    override operator fun plus(other: Gram) = Gram(value + other.value)
    override operator fun minus(other: Gram) = Gram(value - other.value)
    override operator fun times(other: Long) = Gram(value * other)
    override operator fun div(other: Long) = Gram(value / other)
    override operator fun compareTo(other: Gram) = value.compareTo(other.value)
}