package hu.raven.puppet.model.math

import hu.raven.puppet.utility.biggestCommonDivider
import hu.raven.puppet.utility.extention.divByTwoToThePowerOf
import hu.raven.puppet.utility.extention.log2
import hu.raven.puppet.utility.extention.timesTwoToThePowerOf
import java.lang.Integer.min
import kotlin.random.Random

class Fraction private constructor(
    val numerator: Int,
    val denominator: Int,
    val exponential: Int
) : Comparable<Fraction> {

    companion object {
        private const val MAX_LOG = Int.SIZE_BITS - 1
        private const val MAX = Int.MAX_VALUE.toLong()

        fun new(
            numeratorInitialValue: Long,
            denominatorInitialValue: Long,
            exponentialInitialValue: Int
        ): Fraction {
            if (denominatorInitialValue == 0L) {
                throw ArithmeticException("Division by zero!")
            }

            if (denominatorInitialValue < 0L || numeratorInitialValue < 0L) {
                throw ArithmeticException("Negatives are not supported! numerator:$numeratorInitialValue denominator:$denominatorInitialValue exponential:$exponentialInitialValue")
            }

            if (numeratorInitialValue == 0L) {
                return Fraction(0, 1, 0)
            }

            if (
                numeratorInitialValue < MAX &&
                denominatorInitialValue < MAX
            ) {
                return Fraction(numeratorInitialValue.toInt(), denominatorInitialValue.toInt(), exponentialInitialValue)
            }

            var numerator = numeratorInitialValue
            var denominator = denominatorInitialValue
            var exponential = exponentialInitialValue

            var biggestCommonDivider = numerator.biggestCommonDivider(denominator)

            numerator /= biggestCommonDivider
            denominator /= biggestCommonDivider

            if (numerator >= MAX) {
                val logDiff = numerator.log2() - MAX_LOG + 1
                numerator = numerator.divByTwoToThePowerOf(logDiff)
                exponential += logDiff
            }

            if (denominator >= MAX) {
                val logDiff = denominator.log2() - MAX_LOG + 1
                denominator = denominator.divByTwoToThePowerOf(logDiff)
                exponential -= logDiff
            }

            biggestCommonDivider = numerator.biggestCommonDivider(denominator)

            numerator /= biggestCommonDivider
            denominator /= biggestCommonDivider

            return Fraction(numerator.toInt(), denominator.toInt(), exponential)
        }

        fun new(
            numerator: Long,
            denominator: Long
        ) = new(
            numerator,
            denominator,
            0
        )

        fun new(
            numerator: Long
        ) = new(
            numerator,
            1L,
            0
        )

        fun randomUntil(fraction: Fraction): Fraction {
            val maxShift = fraction.numerator.countLeadingZeroBits() - 1
            val numeratorLimit = fraction.numerator.toLong().timesTwoToThePowerOf(maxShift)
            val randomNumerator = Random.nextLong(numeratorLimit)
            return new(
                randomNumerator,
                fraction.denominator.toLong(),
                fraction.exponential - maxShift
            )
        }
    }

    operator fun plus(other: Fraction): Fraction {
        return when {
            exponential == other.exponential -> {
                new(
                    numerator.toLong() * other.denominator + other.numerator.toLong() * denominator,
                    denominator.toLong() * other.denominator,
                    exponential
                )
            }

            numerator == 0 -> other

            other.numerator == 0 -> this

            exponential > other.exponential -> {
                val expDiff = exponential - other.exponential
                val maxShift =
                    min(numerator.countLeadingZeroBits() + other.denominator.countLeadingZeroBits() - 1, expDiff)
                val newNumerator = numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newOtherNumerator = other.numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator * other.denominator + newOtherNumerator * denominator,
                    denominator.toLong() * other.denominator,
                    exponential - maxShift
                )
            }

            else -> {
                val expDiff = other.exponential - exponential
                val maxShift =
                    min(other.numerator.countLeadingZeroBits() + denominator.countLeadingZeroBits() - 1, expDiff)
                val newOtherNumerator = other.numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newNumerator = numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator * other.denominator + newOtherNumerator * denominator,
                    denominator.toLong() * other.denominator,
                    other.exponential - maxShift
                )
            }
        }
    }

    operator fun minus(other: Fraction): Fraction {
        return when {
            exponential == other.exponential -> {
                new(
                    numerator.toLong() * other.denominator - other.numerator.toLong() * denominator,
                    denominator.toLong() * other.denominator,
                    exponential
                )
            }

            numerator == 0 -> other * (-1L)

            other.numerator == 0 -> this

            exponential > other.exponential -> {
                val expDiff = exponential - other.exponential
                val maxShift = min(numerator.countLeadingZeroBits() - 1, expDiff)
                val newNumerator = numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newOtherNumerator = other.numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator * other.denominator - newOtherNumerator * denominator,
                    denominator.toLong() * other.denominator,
                    exponential - maxShift
                )
            }

            else -> {
                val expDiff = other.exponential - exponential
                val maxShift = min(other.numerator.countLeadingZeroBits() - 1, expDiff)
                val newOtherNumerator = other.numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newNumerator = numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator * other.denominator - newOtherNumerator * denominator,
                    denominator.toLong() * other.denominator,
                    other.exponential - maxShift
                )
            }
        }
    }

    operator fun times(other: Fraction): Fraction {
        return new(
            numerator.toLong() * other.numerator,
            denominator.toLong() * other.denominator,
            exponential + other.exponential
        )
    }

    operator fun times(other: Long): Fraction {
        return new(numerator * other, denominator.toLong(), exponential)
    }

    operator fun div(other: Fraction): Fraction {
        return new(
            numerator.toLong() * other.denominator,
            denominator.toLong() * other.numerator,
            exponential - other.exponential
        )
    }

    operator fun div(other: Long): Fraction {
        return new(numerator.toLong(), denominator * other, exponential)
    }

    override operator fun compareTo(other: Fraction): Int {
        return when {
            numerator == 0 && other.numerator == 0 -> 0
            numerator == 0 -> -1
            other.numerator == 0 -> 1

            exponential == other.exponential -> {
                (numerator.toLong() * other.denominator).compareTo(other.numerator.toLong() * denominator)
            }

            exponential > other.exponential && numerator.toLong() * other.denominator >= other.numerator.toLong() * denominator -> 1

            exponential > other.exponential -> {
                val exponentialDif = exponential - other.exponential
                val newNumerator = numerator.toLong().timesTwoToThePowerOf(exponentialDif)
                (newNumerator * other.denominator).compareTo(other.numerator.toLong() * denominator)
            }

            numerator * other.denominator <= other.numerator * denominator -> -1

            else -> {
                val exponentialDif = other.exponential - exponential
                val otherNumerator = other.numerator.toLong().timesTwoToThePowerOf(exponentialDif)
                (numerator.toLong() * other.denominator).compareTo(otherNumerator * denominator)
            }

        }
    }

    fun multiplicativeInverse() = new(denominator.toLong(), numerator.toLong(), -exponential)
    override fun toString(): String {
        return "($numerator / $denominator) * 2^$exponential"
    }
}