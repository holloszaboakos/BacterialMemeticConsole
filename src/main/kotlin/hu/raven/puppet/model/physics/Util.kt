package hu.raven.puppet.model.physics

fun <C : PhysicsUnit<C>> Array<C>.sum() = reduce { left, right -> left + right }