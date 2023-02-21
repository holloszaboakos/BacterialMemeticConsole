package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializeAlgorithm<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    abstract operator fun invoke()
}