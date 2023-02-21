package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class SSpecimenRepresentationFactory<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> {
    abstract fun produce(id: Int, values: Array<IntArray>): S
    abstract fun copy(specimen: S): S
}
