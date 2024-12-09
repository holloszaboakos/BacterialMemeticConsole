package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.AlgorithmSolution

class OppositionOperator<S : AlgorithmSolution<Permutation, S>> : BacterialMutationOperator<Permutation, S> {
    override fun invoke(
        clone: S,
        selectedSegments: Array<ContinuousSegment>
    ) {
        clone.representation.clear()

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
                clone.representation[index] = value
            }
    }
}