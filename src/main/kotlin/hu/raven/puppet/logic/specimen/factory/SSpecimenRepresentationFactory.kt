package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed class SSpecimenRepresentationFactory<S : ISpecimenRepresentation> {
    abstract fun produce(id:Int, values: Array<IntArray>) : S
    abstract fun copy(specimen : S) : S
}
