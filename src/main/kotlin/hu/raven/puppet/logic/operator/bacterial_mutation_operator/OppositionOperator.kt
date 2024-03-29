package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation

data object OppositionOperator : BacterialMutationOperator {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {
        clone.permutation.clear()

        val segmentsReversed = selectedSegments
            .filter { !it.keepInPlace }
            .reversed()

        var counter = -1

        val newSegmentOrder = selectedSegments
            .map {
                if (it.keepInPlace) {
                    it
                } else {
                    counter++
                    segmentsReversed[counter]
                }
            }

        newSegmentOrder
            .flatMap { it.values.toList() }
            .forEachIndexed { index, value ->
                clone.permutation[index] = value
            }
    }
}