package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment<C : PhysicsUnit<C>>(
    override val cloneSegmentLength: Int,
) : SelectSegment<C>() {

    override fun invoke(
        specimen: PoolItem<OnePartRepresentationWithIteration<C>>,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val positions = specimen.content.permutation.indices
            .selectRandomPositions(cloneSegmentLength)
        return Segment(
            positions = positions,
            values = positions.map { specimen.content.permutation[it] }.toIntArray()
        )
    }
}