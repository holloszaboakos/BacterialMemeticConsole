package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class SelectContinuesSegmentWithFullCoverage<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
) : SelectSegment<C>() {
    private val randomizer: IntArray by lazy {
        (0 until parameters.cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: OnePartRepresentation<C>,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment = algorithmState.run {
        val segmentPosition = randomizer[iteration % randomizer.size] + cycleIndex * parameters.cloneSegmentLength
        val selectedPositions = IntArray(parameters.cloneSegmentLength) { segmentPosition + it }
        val selectedElements = selectedPositions
            .map { specimen[it] }
            .toIntArray()
        Segment(
            selectedPositions,
            selectedElements
        )
    }
}