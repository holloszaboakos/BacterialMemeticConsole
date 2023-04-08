package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.asPermutation


class RandomShuffleOfContinuesSegment<C : PhysicsUnit<C>> : BacterialMutationOperator<C>() {
    override fun invoke(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ) {
        selectedSegment.positions
            .asPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone.permutation[writeIndex] = selectedSegment.values[readIndex]
            }
    }
}