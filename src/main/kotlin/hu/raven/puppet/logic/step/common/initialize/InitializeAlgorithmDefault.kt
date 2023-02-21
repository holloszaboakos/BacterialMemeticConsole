package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class InitializeAlgorithmDefault<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {

    override operator fun invoke() {}
}