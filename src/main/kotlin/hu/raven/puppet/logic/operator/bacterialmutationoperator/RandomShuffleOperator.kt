package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation

data object RandomShuffleOperator : BacterialMutationOperator {

    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {
        clone.permutation.clear()

        val segmentsShuffled = selectedSegments
            .filter { !it.keepInPlace }
            .shuffled()

        var counter = -1

        val newSegmentOrder = selectedSegments
            .map {
                if (it.keepInPlace) {
                    it
                } else {
                    counter++
                    segmentsShuffled[counter]
                }
            }

        newSegmentOrder
            .flatMap { it.values.toList() }
            .forEachIndexed { index, value ->
                clone.permutation[index] = value
            }
    }
}