package hu.raven.puppet.utility.extention

import kotlin.math.sqrt

object FloatArrayExtensions {

    operator fun FloatArray.compareTo(right: FloatArray): Int = when {
        indices.all { get(it) >= right[it] } &&
                indices.any { get(it) > right[it] } -> 1

        indices.all { get(it) <= right[it] } &&
                indices.any { get(it) < right[it] } -> -1

        else -> 0
    }

    fun FloatArray.vectorLength(): Float =
        map { it * it }.sum().let(::sqrt)

    infix fun FloatArray.matches(right: FloatArray): Boolean =
        indices.all { get(it) == right[it] }
}