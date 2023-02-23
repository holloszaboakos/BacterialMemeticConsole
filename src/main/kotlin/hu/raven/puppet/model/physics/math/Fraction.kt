package hu.raven.puppet.model.physics.math

import hu.raven.puppet.utility.biggestCommonDivider

@JvmInline
value class Fraction private constructor(val value: Pair<Long, Long>) {

    constructor() : this(Pair(0, 1))
    constructor(value: Long) : this(Pair(value, 1))
    constructor(first: Long, second: Long) : this(Pair(first, second))

    operator fun plus(other: Fraction): Fraction {
        val result = Fraction(
            Pair(
                value.first * other.value.second + other.value.first * value.second,
                value.second * other.value.second
            )
        )

        return if (result.value.first < Int.MAX_VALUE && result.value.second < Int.MAX_VALUE)
            result
        else
            result.simplify()
    }

    operator fun minus(other: Fraction): Fraction {
        val result = Fraction(
            Pair(
                value.first * other.value.second - other.value.first * value.second,
                value.second * other.value.second
            )
        )

        return if (result.value.first < Int.MAX_VALUE && result.value.second < Int.MAX_VALUE)
            result
        else
            result.simplify()
    }

    operator fun times(other: Fraction) :Fraction {
        val result = Fraction(
            Pair(
                value.first * other.value.first,
                value.second * other.value.second
            )
        )

        return if (result.value.first < Int.MAX_VALUE && result.value.second < Int.MAX_VALUE)
            result
        else
            result.simplify()
    }
    operator fun times(other: Long) :Fraction {
        val result = Fraction(value.first * other, value.second)

        return if (result.value.first < Int.MAX_VALUE && result.value.second < Int.MAX_VALUE)
            result
        else
            result.simplify()
    }

    operator fun div(other: Fraction) :Fraction {
        val result = Fraction(
            Pair(
                value.first * other.value.second,
                value.second * other.value.first
            )
        )

        return if (result.value.first < Int.MAX_VALUE && result.value.second < Int.MAX_VALUE)
            result
        else
            result.simplify()
    }

    operator fun div(other: Long) :Fraction {
        val result = Fraction(value.first, value.second * other)

        return if (result.value.first < Int.MAX_VALUE && result.value.second < Int.MAX_VALUE)
            result
        else
            result.simplify()
    }

    operator fun compareTo(other: Fraction) =
        (value.first * other.value.second).compareTo(value.second * other.value.first)

    fun toDouble() = value.first / value.second.toDouble()

    private fun simplify():Fraction{
        val biggestCommonDivider = value.first.biggestCommonDivider(value.second)
        return Fraction(
            value.first / biggestCommonDivider,
            value.second / biggestCommonDivider
        )
    }
}