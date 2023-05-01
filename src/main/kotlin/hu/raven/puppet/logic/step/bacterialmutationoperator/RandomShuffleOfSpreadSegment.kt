package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment

class RandomShuffleOfSpreadSegment : BacterialMutationOperator() {

    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegment: Segment
    ) {
        val shuffler = (0 until selectedSegment.positions.size).shuffled()
        selectedSegment.positions.forEachIndexed { index, position ->
            clone.permutation[position] = selectedSegment.values[shuffler[index]]
        }
    }
}