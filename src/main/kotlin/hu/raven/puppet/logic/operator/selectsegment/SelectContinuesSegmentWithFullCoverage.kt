package hu.raven.puppet.logic.operator.selectsegment

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.Segment

class SelectContinuesSegmentWithFullCoverage(
    override val cloneSegmentLength: Int,
) : SelectSegment() {
    private val randomizer: IntArray by lazy {
        (0 until cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val segmentPosition = randomizer[iteration % randomizer.size] + cycleIndex * cloneSegmentLength
        val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        return Segment(
            selectedPositions,
            selectedElements
        )
    }
}