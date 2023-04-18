package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.toPermutation


class RandomShuffleOfContinuesSegment<C : PhysicsUnit<C>> : BacterialMutationOperator<C>() {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegment: Segment
    ) {
        selectedSegment.positions.forEach { clone.permutation.deletePosition(it) }
        selectedSegment.positions
            .toPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone.permutation[writeIndex] = selectedSegment.values[readIndex]
            }
    }
}