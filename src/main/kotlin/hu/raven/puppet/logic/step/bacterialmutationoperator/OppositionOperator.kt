package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class OppositionOperator<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
) :
    BacterialMutationOperator<C>() {
    override fun invoke(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ) {
        selectedSegment.positions.forEachIndexed { readIndex, writeIndex ->
            clone[writeIndex] = selectedSegment.values[selectedSegment.values.size - 1 - readIndex]
        }
    }
}