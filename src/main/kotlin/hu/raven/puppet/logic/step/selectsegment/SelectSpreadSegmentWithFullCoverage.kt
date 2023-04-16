package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment

class SelectSpreadSegmentWithFullCoverage<C : PhysicsUnit<C>>(
    override val cloneSegmentLength: Int,
) : SelectSegment<C>() {

    private var randomPermutation: IntArray? = null

    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        if (randomPermutation == null) {
            randomPermutation =
                IntArray(specimen.size) { it }
                    .apply { shuffle() }
        }
        val segmentStart = cycleIndex * cloneSegmentLength
        val segmentEnd = (cycleIndex + 1) * cloneSegmentLength
        val selectedPositions = randomPermutation!!
            .slice(segmentStart until segmentEnd)
            .sortedBy { it }
            .toIntArray()
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        return Segment(selectedPositions, selectedElements)
    }
}