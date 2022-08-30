package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.DTwoPartRepresentation

class OTwoPartRepresentationFactory : SSpecimenRepresentationFactory<DTwoPartRepresentation>() {
    override fun produce(id: Int, values: Array<IntArray>): DTwoPartRepresentation = DTwoPartRepresentation(id, values)
    override fun copy(specimen: DTwoPartRepresentation) = DTwoPartRepresentation(specimen)
}