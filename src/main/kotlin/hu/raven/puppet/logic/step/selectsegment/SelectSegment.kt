package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.solution.Segment

sealed class SelectSegment<C : PhysicsUnit<C>> {
    abstract val cloneSegmentLength: Int

    abstract operator fun invoke(
        specimen: PoolItem<OnePartRepresentationWithIteration<C>>,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment
}