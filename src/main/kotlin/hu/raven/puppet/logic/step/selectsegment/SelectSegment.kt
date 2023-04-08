package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class SelectSegment<C : PhysicsUnit<C>> {
    abstract val cloneSegmentLength: Int

    abstract operator fun invoke(
        specimen: OnePartRepresentation<C>,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment
}