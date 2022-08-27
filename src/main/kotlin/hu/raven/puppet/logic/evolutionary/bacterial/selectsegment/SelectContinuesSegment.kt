package hu.raven.puppet.logic.evolutionary.bacterial.selectsegment

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random

class SelectContinuesSegment : SelectSegment{
    override fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        specimen: S,
        cloneSegmentLength: Int
    ) : IntArray {
        val randomPosition =
            Random.nextSegmentStartPosition(
                specimen.permutationIndices.count(),
                cloneSegmentLength
            )
        return IntArray(cloneSegmentLength) { randomPosition + it }
    }
}