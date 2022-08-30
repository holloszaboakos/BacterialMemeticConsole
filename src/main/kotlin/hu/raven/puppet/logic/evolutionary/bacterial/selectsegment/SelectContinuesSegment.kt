package hu.raven.puppet.logic.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random

class SelectContinuesSegment<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>
) : SelectSegment<S> {
    override fun invoke(
        specimen: S
    ): IntArray {
        val randomPosition =
            Random.nextSegmentStartPosition(
                specimen.permutationIndices.count(),
                algorithm.cloneSegmentLength
            )
        return IntArray(algorithm.cloneSegmentLength) { randomPosition + it }
    }
}