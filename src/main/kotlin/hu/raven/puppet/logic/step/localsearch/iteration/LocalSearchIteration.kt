package hu.raven.puppet.logic.step.localsearch.iteration

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.localsearch.LocalSearchStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class LocalSearchIteration<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : LocalSearchStep<S, C>() {

    abstract operator fun invoke()
}