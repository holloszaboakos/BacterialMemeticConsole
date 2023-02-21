package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class OnePartRepresentationFactory<C : PhysicsUnit<C>> :
    SSpecimenRepresentationFactory<DOnePartRepresentation<C>, C>() {
    override fun produce(id: Int, values: Array<IntArray>): DOnePartRepresentation<C> =
        DOnePartRepresentation(id, values)

    override fun copy(specimen: DOnePartRepresentation<C>) = DOnePartRepresentation(specimen)
}