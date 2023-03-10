package hu.raven.puppet.model.math

import hu.raven.puppet.utility.extention.divByTwoToThePowerOf
import hu.raven.puppet.utility.extention.log2
import hu.raven.puppet.utility.extention.timesTwoToThePowerOf
import kotlin.math.pow

data class Exponential private constructor(
    val numerator: Long,
    val exponential: Int
) {

    companion object {
        private const val MAX_LOG = 31
        private val MAX = 1L.timesTwoToThePowerOf(MAX_LOG)

        fun new(
            numeratorInitialValue: Long,
            exponentialInitialValue: Int
        ): Exponential {
            if (numeratorInitialValue < 0L) {
                throw ArithmeticException("Negatives are not supported! numerator:$numeratorInitialValue exponential:$exponentialInitialValue")
            }

            if (
                numeratorInitialValue <= MAX
            ) {
                return Exponential(numeratorInitialValue, exponentialInitialValue)
            }

            var numerator = numeratorInitialValue
            var exponential = exponentialInitialValue

            val logDiff = numerator.log2() - MAX_LOG + 1
            numerator = numerator.divByTwoToThePowerOf(logDiff)
            exponential += logDiff

            return Exponential(numerator, exponential)
        }

        fun new(numerator: Long) = new(numerator, 0)

    }

    operator fun plus(other: Exponential): Exponential {
        return when {
            exponential == other.exponential -> {
                new(numerator + other.numerator, exponential)
            }

            exponential > other.exponential -> {
                val expDiff = exponential - other.exponential
                if (expDiff > 20) {
                    return this
                }
                val maxIncrease = numerator.countLeadingZeroBits() - 2

                val newNumerator = if (expDiff <= maxIncrease) {
                    numerator.timesTwoToThePowerOf(expDiff)
                } else {
                    numerator.timesTwoToThePowerOf(maxIncrease)
                }

                val otherNumerator = if (expDiff <= maxIncrease) {
                    other.numerator
                } else {
                    other.numerator.divByTwoToThePowerOf(expDiff - maxIncrease)
                }

                val newExponential = if (expDiff <= maxIncrease) {
                    other.exponential
                } else {
                    other.exponential + expDiff - maxIncrease
                }

                new(
                    newNumerator + otherNumerator,
                    newExponential
                )
            }

            else -> {
                val expDiff = other.exponential - exponential
                if (expDiff > 20) {
                    return this
                }
                val maxIncrease = other.numerator.countLeadingZeroBits() - 2

                val otherNumerator = if (expDiff <= maxIncrease) {
                    other.numerator.timesTwoToThePowerOf(expDiff)
                } else {
                    other.numerator.timesTwoToThePowerOf(maxIncrease)
                }

                val newNumerator = if (expDiff <= maxIncrease) {
                    numerator
                } else {
                    numerator.divByTwoToThePowerOf(expDiff - maxIncrease)
                }
                val newExponential = if (expDiff <= maxIncrease) {
                    exponential
                } else {
                    exponential + expDiff - maxIncrease
                }

                new(
                    newNumerator + otherNumerator,
                    newExponential
                )
            }
        }
    }

    operator fun minus(other: Exponential): Exponential {
        return when {
            exponential == other.exponential -> {
                new(numerator - other.numerator, exponential)
            }

            exponential > other.exponential -> {
                val expDiff = exponential - other.exponential
                if (expDiff > 20) {
                    return this
                }
                val maxIncrease = numerator.countLeadingZeroBits() - 2

                val newNumerator = if (expDiff <= maxIncrease) {
                    numerator.timesTwoToThePowerOf(expDiff)
                } else {
                    numerator.timesTwoToThePowerOf(maxIncrease)
                }

                val otherNumerator = if (expDiff <= maxIncrease) {
                    other.numerator
                } else {
                    other.numerator.divByTwoToThePowerOf(expDiff - maxIncrease)
                }

                val newExponential = if (expDiff <= maxIncrease) {
                    other.exponential
                } else {
                    other.exponential + expDiff - maxIncrease
                }

                new(
                    newNumerator - otherNumerator,
                    newExponential
                )
            }

            else -> {
                val expDiff = other.exponential - exponential
                if (expDiff > 20) {
                    return this
                }
                val maxIncrease = other.numerator.countLeadingZeroBits() - 2

                val otherNumerator = if (expDiff <= maxIncrease) {
                    other.numerator.timesTwoToThePowerOf(expDiff)
                } else {
                    other.numerator.timesTwoToThePowerOf(maxIncrease)
                }

                val newNumerator = if (expDiff <= maxIncrease) {
                    numerator
                } else {
                    numerator.divByTwoToThePowerOf(expDiff - maxIncrease)
                }
                val newExponential = if (expDiff <= maxIncrease) {
                    exponential
                } else {
                    exponential + expDiff - maxIncrease
                }

                new(
                    newNumerator - otherNumerator,
                    newExponential
                )
            }
        }
    }

    operator fun times(other: Exponential): Exponential {
        return new(
            numerator * other.numerator,
            exponential + other.exponential
        )
    }

    operator fun times(other: Long): Exponential {
        return new(numerator * other, exponential)
    }

    operator fun div(other: Exponential): Exponential {
        //TODO minimal loss div
        return new(
            numerator / other.numerator,
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
            numerator == 0L && other.numerator == 0L -> 0

            exponential == other.exponential -> {
                numerator.compareTo(other.numerator)
            }

            exponential > other.exponential -> when {
                numerator >= other.numerator -> 1
                else -> {
                    val exponentialDif = exponential - other.exponential
                    val newNumerator = numerator.timesTwoToThePowerOf(exponentialDif)
                    (newNumerator).compareTo(other.numerator)
                }
            }

            else -> when {
                numerator <= other.numerator -> -1
                else -> {
                    val exponentialDif = other.exponential - exponential
                    val otherNumerator = other.numerator.timesTwoToThePowerOf(exponentialDif)
                    numerator.compareTo(otherNumerator)
                }
            }
        }
    }
}