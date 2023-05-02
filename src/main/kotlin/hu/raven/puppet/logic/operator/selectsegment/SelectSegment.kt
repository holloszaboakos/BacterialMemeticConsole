package hu.raven.puppet.logic.operator.selectsegment

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.solution.Segment

sealed class SelectSegment {
    abstract val cloneSegmentLength: Int

    abstract operator fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment
}