package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.AlgorithmSolution

sealed interface BacterialMutationOperator<R, S : AlgorithmSolution<R, S>> {
    operator fun invoke(
        clone: S,
        selectedSegments: Array<ContinuousSegment>,
    )
}