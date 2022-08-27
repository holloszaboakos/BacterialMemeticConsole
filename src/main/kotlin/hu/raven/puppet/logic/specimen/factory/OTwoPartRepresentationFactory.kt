package hu.raven.puppet.logic.specimen.factory

import hu.raven.puppet.logic.specimen.DTwoPartRepresentation
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory

class OTwoPartRepresentationFactory : SSpecimenRepresentationFactory<DTwoPartRepresentation>() {
    override fun produce(id:Int,values: Array<IntArray>): DTwoPartRepresentation = DTwoPartRepresentation(id, values)
    override fun copy(specimen: DTwoPartRepresentation) = DTwoPartRepresentation(specimen)
}