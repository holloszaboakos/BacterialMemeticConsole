package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation

class SelectSpreadSegmentWithFullCoverage<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : SelectSegment<S, C>() {

    private val randomPermutation: IntArray by lazy {
        IntArray(geneCount) { it }
            .apply { shuffle() }
    }

    override fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val segmentStart = cycleIndex * cloneSegmentLength
        val segmentEnd = (cycleIndex + 1) * cloneSegmentLength
        val selectedPositions = randomPermutation
            .slice(segmentStart until segmentEnd)
            .sortedBy { it }
            .toIntArray()
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        return Segment(selectedPositions, selectedElements)
    }
}