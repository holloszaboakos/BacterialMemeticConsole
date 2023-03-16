package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : SelectSegment<S, C>() {

    override fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val positions = specimen.permutationIndices
            .selectRandomPositions(cloneSegmentLength)
        return Segment(
            positions = positions,
            values = positions.map { specimen[it] }.toIntArray()
        )
    }
}