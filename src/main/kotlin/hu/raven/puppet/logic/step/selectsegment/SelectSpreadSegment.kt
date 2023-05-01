package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment(
    override val cloneSegmentLength: Int,
) : SelectSegment() {

    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val positions = specimen.indices
            .selectRandomPositions(cloneSegmentLength)
        return Segment(
            positions = positions,
            values = positions.map { specimen[it] }.toIntArray()
        )
    }
}