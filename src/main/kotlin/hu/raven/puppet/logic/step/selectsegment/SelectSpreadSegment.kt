package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.selectRandomPositions

class SelectSpreadSegment<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
) : SelectSegment<C>() {

    override fun invoke(
        specimen: OnePartRepresentation<C>,
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