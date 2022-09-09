package hu.raven.puppet.logic.step.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random

class SelectContinuesSegment<S : ISpecimenRepresentation> : SelectSegment<S>() {
    override fun invoke(
        specimen: S
    ): IntArray {
        val randomPosition =
            Random.nextSegmentStartPosition(
                specimen.permutationIndices.count(),
                cloneSegmentLength
            )
        return IntArray(cloneSegmentLength) { randomPosition + it }
    }
}