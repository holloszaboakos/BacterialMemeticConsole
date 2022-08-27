package hu.raven.puppet.logic.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment : SelectSegment{
    override fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        specimen: S,
        cloneSegmentLength: Int
    ) : IntArray {
        return specimen.permutationIndices
            .selectRandomPositions(cloneSegmentLength)
    }
}