package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class SelectSpreadSegmentWithFullCoverage<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
) : SelectSegment<C>() {

    private val randomPermutation: IntArray by lazy {
        IntArray(parameters.geneCount) { it }
            .apply { shuffle() }
    }

    override fun invoke(
        specimen: OnePartRepresentation<C>,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val segmentStart = cycleIndex * parameters.cloneSegmentLength
        val segmentEnd = (cycleIndex + 1) * parameters.cloneSegmentLength
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