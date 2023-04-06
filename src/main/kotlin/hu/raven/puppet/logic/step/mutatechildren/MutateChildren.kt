package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.physics.PhysicsUnit

sealed class MutateChildren<C : PhysicsUnit<C>> {
    abstract operator fun invoke()
}