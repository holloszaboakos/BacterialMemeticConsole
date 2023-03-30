package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
) : SelectSegment<S, C>() {

    override fun invoke(
        specimen: S,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment {
        val positions = specimen.permutationIndices
            .selectRandomPositions(parameters.cloneSegmentLength)
        return Segment(
            positions = positions,
            values = positions.map { specimen[it] }.toIntArray()
        )
    }
}