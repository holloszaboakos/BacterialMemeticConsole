package hu.raven.puppet.logic.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface SelectSegment{
    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        specimen: S,
        cloneSegmentLength: Int
    ) : IntArray
}