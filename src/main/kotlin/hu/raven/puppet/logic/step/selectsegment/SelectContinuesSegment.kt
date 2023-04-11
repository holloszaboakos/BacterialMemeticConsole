package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random

class SelectContinuesSegment<C : PhysicsUnit<C>>(
    override val cloneSegmentLength: Int,
) : SelectSegment<C>() {
    override fun invoke(
        specimen: PoolItem<OnePartRepresentationWithIteration<C>>,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val randomPosition =
            Random.nextSegmentStartPosition(
                specimen.content.permutation.indices.count(),
                cloneSegmentLength
            )
        val positions = IntArray(cloneSegmentLength) { randomPosition + it }
        return Segment(
            positions = positions,
            values = positions.map { specimen.content.permutation[it] }.toIntArray()
        )
    }
}