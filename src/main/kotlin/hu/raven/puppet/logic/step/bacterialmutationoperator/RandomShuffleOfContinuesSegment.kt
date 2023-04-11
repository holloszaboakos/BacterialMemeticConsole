package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.toPermutation


class RandomShuffleOfContinuesSegment<C : PhysicsUnit<C>> : BacterialMutationOperator<C>() {
    override fun invoke(
        clone: PoolItem<OnePartRepresentationWithIteration<C>>,
        selectedSegment: Segment
    ) {
        selectedSegment.positions
            .toPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone.content.permutation[writeIndex] = selectedSegment.values[readIndex]
            }
    }
}