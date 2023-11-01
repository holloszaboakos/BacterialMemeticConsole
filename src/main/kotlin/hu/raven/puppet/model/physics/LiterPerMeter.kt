package hu.raven.puppet.model.physics


@JvmInline
value class LiterPerMeter(override val value: Float) : PhysicsUnit<LiterPerMeter> {
    constructor(value: Int) : this(value.toFloat())
    constructor(first: Int, second: Int) : this(first / second.toFloat())

    override operator fun plus(other: LiterPerMeter) = LiterPerMeter(value + other.value)
    override operator fun minus(other: LiterPerMeter) = LiterPerMeter(value - other.value)
    override operator fun times(other: Long) = LiterPerMeter(value * other)
    override operator fun div(other: Long) = LiterPerMeter(value / other)
    override operator fun compareTo(other: LiterPerMeter) = value.compareTo(other.value)
}