package hu.raven.puppet.logic.step.localsearchiteration

import hu.raven.puppet.model.physics.PhysicsUnit

sealed class LocalSearchIteration<C : PhysicsUnit<C>> {

    abstract operator fun invoke()
}