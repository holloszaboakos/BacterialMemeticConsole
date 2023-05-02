package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.utility.extention.toPermutation


object RandomShuffleOfContinuesSegment : BacterialMutationOperator() {
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