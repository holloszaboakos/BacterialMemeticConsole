package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class NoBoost<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : Boost<S, C>() {
    override suspend operator fun invoke() {
    }
}