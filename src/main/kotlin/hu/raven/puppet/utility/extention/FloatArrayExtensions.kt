package hu.raven.puppet.utility.extention

import kotlin.math.sqrt

object FloatArrayExtensions {

    fun FloatArray.vectorLength(): Float =
        map { it * it }.sum().let(::sqrt)

    infix fun FloatArray.matches(right: FloatArray): Boolean =
        indices.all { get(it) == right[it] }

    infix fun FloatArray.subordinatedBy(right: FloatArray): Boolean =
        right.dominatedBy(this)

    infix fun FloatArray.notSubordinatedBy(right: FloatArray): Boolean =
        right.notDominatedBy(this)

    private infix fun FloatArray.dominatedBy(right: FloatArray): Boolean =
        indices.all { get(it) <= right[it] } &&
                indices.any { get(it) < right[it] }

    private infix fun FloatArray.notDominatedBy(right: FloatArray): Boolean =
        !dominatedBy(right)
}