package hu.raven.puppet.logic.operator.selectsegments

import hu.raven.puppet.model.math.Permutation

//identify segments that can move
//each segment consists of a range and elements
sealed interface SelectSegments {
    val cloneSegmentLength: Int

    operator fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Array<ContinuousSegment>
}