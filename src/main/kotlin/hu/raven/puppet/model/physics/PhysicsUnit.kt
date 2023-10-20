package hu.raven.puppet.model.physics

sealed interface PhysicsUnit<S : PhysicsUnit<S>> : Comparable<S> {
    val value: Float
    operator fun plus(other: S): S
    operator fun minus(other: S): S
    operator fun times(other: Long): S
    operator fun div(other: Long): S
    override operator fun compareTo(other: S): Int
}