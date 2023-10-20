package hu.raven.puppet.model.math

import hu.raven.puppet.utility.extention.divByTwoToThePowerOf
import hu.raven.puppet.utility.extention.log2
import hu.raven.puppet.utility.extention.timesTwoToThePowerOf
import kotlin.math.pow

private class Exponential private constructor(
    val numerator: Int,
    val exponential: Int
) {

    companion object {

        fun new(
            numeratorInitialValue: Long,
            exponentialInitialValue: Int
        ): Exponential {
            if (numeratorInitialValue < 0L) {
                throw ArithmeticException("Negatives are not supported! numerator:$numeratorInitialValue exponential:$exponentialInitialValue")
            }

            if (
                numeratorInitialValue <= Int.MAX_VALUE
            ) {
                return Exponential(numeratorInitialValue.toInt(), exponentialInitialValue)
            }

            var numerator = numeratorInitialValue
            var exponential = exponentialInitialValue

            val logDiff = numerator.log2() - Int.SIZE_BITS + 1
            numerator = numerator.divByTwoToThePowerOf(logDiff)
            exponential += logDiff

            return Exponential(numerator.toInt(), exponential)
        }

        fun new(numerator: Long) = new(numerator, 0)

    }

    operator fun plus(other: Exponential): Exponential {
        return when {
            exponential == other.exponential -> {
                new(
                    numerator.toLong() + other.numerator,
                    exponential
                )
            }

            numerator == 0 -> other

            other.numerator == 0 -> this

            exponential > other.exponential -> {
                val expDiff = exponential - other.exponential
                val maxShift =
                    Integer.min(
                        numerator.countLeadingZeroBits() - 1,
                        expDiff
                    )
                val newNumerator = numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newOtherNumerator = other.numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator + newOtherNumerator,
                    exponential - maxShift
                )
            }

            else -> {
                val expDiff = other.exponential - exponential
                val maxShift =
                    Integer.min(
                        other.numerator.countLeadingZeroBits() - 1,
                        expDiff
                    )
                val newOtherNumerator = other.numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newNumerator = numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator + newOtherNumerator,
                    other.exponential - maxShift
                )
            }
        }
    }

    operator fun minus(other: Exponential): Exponential {
        return when {
            exponential == other.exponential -> {
                new(
                    numerator.toLong() - other.numerator.toLong(),
                    exponential
                )
            }

            numerator == 0 -> other * (-1L)

            other.numerator == 0 -> this

            exponential > other.exponential -> {
                val expDiff = exponential - other.exponential
                val maxShift =
                    Integer.min(
                        numerator.countLeadingZeroBits() - 1,
                        expDiff
                    )
                val newNumerator = numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newOtherNumerator = other.numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator - newOtherNumerator,
                    exponential - maxShift
                )
            }

            else -> {
                val expDiff = other.exponential - exponential
                val maxShift =
                    Integer.min(
                        other.numerator.countLeadingZeroBits() - 1,
                        expDiff
                    )
                val newOtherNumerator = other.numerator.toLong().timesTwoToThePowerOf(maxShift)
                val newNumerator = numerator.toLong().divByTwoToThePowerOf(expDiff - maxShift)
                new(
                    newNumerator - newOtherNumerator,
                    other.exponential - maxShift
                )
            }
        }
    }

    operator fun times(other: Exponential): Exponential {
        return new(
            numerator.toLong() * other.numerator,
            exponential + other.exponential
        )
    }

    operator fun times(other: Long): Exponential {
        return new(numerator * other, exponential)
    }

    operator fun div(other: Exponential): Exponential {
        //TODO minimal loss div
        return new(
            numerator.toLong() / other.numerator,
            exponential - other.exponential
        )
    }

    operator fun div(other: Long): Exponential {
        //TODO minimal loss div
        return new(numerator / other, exponential)
    }

    fun toDouble() = 2.0.pow(exponential) * numerator

    operator fun compareTo(other: Exponential): Int {
        return when {
            numerator == 0 && other.numerator == 0 -> 0

            exponential == other.exponential -> {
                numerator.compareTo(other.numerator)
            }

            exponential > other.exponential -> when {
                numerator >= other.numerator -> 1
                else -> {
                    val exponentialDif = exponential - other.exponential
                    val newNumerator = numerator.toLong().timesTwoToThePowerOf(exponentialDif)
                    (newNumerator).compareTo(other.numerator)
                }
            }

            else -> when {
                numerator <= other.numerator -> -1
                else -> {
                    val exponentialDif = other.exponential - exponential
                    val otherNumerator = other.numerator.toLong().timesTwoToThePowerOf(exponentialDif)
                    numerator.compareTo(otherNumerator)
                }
            }
        }
    }
}