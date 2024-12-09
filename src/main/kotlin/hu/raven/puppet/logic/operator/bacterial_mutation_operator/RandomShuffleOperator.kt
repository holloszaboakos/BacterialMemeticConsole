package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.AlgorithmSolution

class RandomShuffleOperator<S : AlgorithmSolution<Permutation, S>> : BacterialMutationOperator<Permutation, S> {

    override fun invoke(
        clone: S,
        selectedSegments: Array<ContinuousSegment>
    ) {
        clone.representation.clear()

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
                clone.representation[index] = value
            }
    }
}