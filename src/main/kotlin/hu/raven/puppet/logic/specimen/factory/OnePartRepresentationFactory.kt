package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.DOnePartRepresentation

class OnePartRepresentationFactory : SSpecimenRepresentationFactory<DOnePartRepresentation>() {
    override fun produce(id: Int, values: Array<IntArray>): DOnePartRepresentation = DOnePartRepresentation(id, values)
    override fun copy(specimen: DOnePartRepresentation) = DOnePartRepresentation(specimen)
}