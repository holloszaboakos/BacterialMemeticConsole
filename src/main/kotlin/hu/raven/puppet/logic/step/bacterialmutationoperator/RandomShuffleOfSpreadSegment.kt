package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class RandomShuffleOfSpreadSegment<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
) : BacterialMutationOperator<C>() {

    override fun invoke(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ) {

        val shuffler = (0 until parameters.cloneSegmentLength).shuffled()
        selectedSegment.positions.forEachIndexed { index, position ->
            clone.permutation[position] = selectedSegment.values[shuffler[index]]
        }
    }
}