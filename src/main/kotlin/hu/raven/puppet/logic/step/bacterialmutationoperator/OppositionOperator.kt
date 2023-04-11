package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.solution.Segment

class OppositionOperator<C : PhysicsUnit<C>> :
    BacterialMutationOperator<C>() {
    override fun invoke(
        clone: PoolItem<OnePartRepresentationWithIteration<C>>,
        selectedSegment: Segment
    ) {
        selectedSegment.positions.forEachIndexed { readIndex, writeIndex ->
            clone.content.permutation[writeIndex] = selectedSegment.values[selectedSegment.values.size - 1 - readIndex]
        }
    }
}