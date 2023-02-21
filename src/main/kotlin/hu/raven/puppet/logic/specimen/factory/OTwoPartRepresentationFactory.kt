package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.DTwoPartRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class OTwoPartRepresentationFactory<C : PhysicsUnit<C>> :
    SSpecimenRepresentationFactory<DTwoPartRepresentation<C>, C>() {
    override fun produce(id: Int, values: Array<IntArray>): DTwoPartRepresentation<C> =
        DTwoPartRepresentation(id, values)

    override fun copy(specimen: DTwoPartRepresentation<C>) = DTwoPartRepresentation(specimen)
}