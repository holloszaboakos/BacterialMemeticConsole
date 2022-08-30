package hu.raven.puppet.logic.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>
) : SelectSegment<S> {

    override fun invoke(
        specimen: S
    ): IntArray {
        return specimen.permutationIndices
            .selectRandomPositions(algorithm.cloneSegmentLength)
    }
}