package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.asPermutation


class RandomShuffleOfContinuesSegment<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
) :
    BacterialMutationOperator<C>() {
    override fun invoke(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ) {
        selectedSegment.positions
            .asPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedSegment.values[readIndex]
            }
    }
}