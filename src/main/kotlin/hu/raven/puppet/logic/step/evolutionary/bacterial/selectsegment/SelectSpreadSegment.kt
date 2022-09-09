package hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment<S : ISpecimenRepresentation> : SelectSegment<S>() {

    override fun invoke(
        specimen: S
    ): IntArray {
        return specimen.permutationIndices
            .selectRandomPositions(cloneSegmentLength)
    }
}