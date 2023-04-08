package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment

class SelectContinuesSegmentWithFullCoverage<C : PhysicsUnit<C>>(
    override val cloneSegmentLength: Int,
) : SelectSegment<C>() {
    private val randomizer: IntArray by lazy {
        (0 until cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: OnePartRepresentation<C>,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val segmentPosition = randomizer[iteration % randomizer.size] + cycleIndex * cloneSegmentLength
        val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
        val selectedElements = selectedPositions
            .map { specimen.permutation[it] }
            .toIntArray()
        return Segment(
            selectedPositions,
            selectedElements
        )
    }
}