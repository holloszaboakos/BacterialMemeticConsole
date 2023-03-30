package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class SelectContinuesSegmentWithFullCoverage<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
) : SelectSegment<S, C>() {
    private val randomizer: IntArray by lazy {
        (0 until parameters.cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: S,
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