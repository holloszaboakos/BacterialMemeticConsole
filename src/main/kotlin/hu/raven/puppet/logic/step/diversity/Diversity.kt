package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.physics.PhysicsUnit

sealed class Diversity<C : PhysicsUnit<C>>{
    abstract operator fun invoke(): Double
}