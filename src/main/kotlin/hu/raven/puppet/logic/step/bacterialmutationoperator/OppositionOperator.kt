package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

import hu.raven.puppet.model.solution.Segment

class OppositionOperator<C : PhysicsUnit<C>> : BacterialMutationOperator<C>() {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegment: Segment
    ) {
        selectedSegment.positions.forEach { clone.permutation.deletePosition(it) }
        selectedSegment.positions.forEachIndexed { readIndex, writeIndex ->
            clone.permutation[writeIndex] = selectedSegment.values[selectedSegment.values.size - 1 - readIndex]
        }
    }
}