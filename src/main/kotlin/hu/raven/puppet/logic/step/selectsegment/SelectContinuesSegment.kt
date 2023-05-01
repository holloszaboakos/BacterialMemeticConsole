package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random

class SelectContinuesSegment(
    override val cloneSegmentLength: Int,
) : SelectSegment() {
    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val randomPosition =
            Random.nextSegmentStartPosition(
                specimen.indices.count(),
                cloneSegmentLength
            )
        val positions = IntArray(cloneSegmentLength) { randomPosition + it }
        return Segment(
            positions = positions,
            values = positions.map { specimen[it] }.toIntArray()
        )
    }
}