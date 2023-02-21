package hu.raven.puppet.model.physics.math

@JvmInline
value class Fraction private constructor(val value: Pair<Long, Long>) {

    constructor() : this(Pair(0, 1))
    constructor(value: Long) : this(Pair(value, 1))
    constructor(first: Long, second: Long) : this(Pair(first, second))

    operator fun plus(other: Fraction) = Fraction(
        Pair(
            value.first * other.value.second + other.value.first * value.second,
            value.second * other.value.second
        )
    )

    operator fun times(other: Fraction) = Fraction(
        Pair(
            value.first * other.value.first,
            value.second * other.value.second
        )
    )

    operator fun times(other: Long) = Fraction(value.first * other, value.second)

    operator fun div(other: Fraction) = Fraction(
        Pair(
            value.first * other.value.second,
            value.second * other.value.first
        )
    )

    operator fun div(other: Long) = Fraction(value.first, value.second * other)

    operator fun compareTo(other: Fraction) =
        (value.first * other.value.second).compareTo(value.second * other.value.first)

    fun toDouble() = value.first / value.second.toDouble()
}