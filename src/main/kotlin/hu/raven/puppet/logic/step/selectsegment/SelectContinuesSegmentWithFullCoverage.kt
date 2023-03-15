package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class SelectContinuesSegmentWithFullCoverage<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    SelectSegment<S, C>() {
    private val randomizer: IntArray by lazy {
        (0 until cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment = algorithmState.run {
        val segmentPosition = randomizer[iteration % randomizer.size] + cycleIndex * cloneSegmentLength
        val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        Segment(
            selectedPositions,
            selectedElements
        )
    }
}