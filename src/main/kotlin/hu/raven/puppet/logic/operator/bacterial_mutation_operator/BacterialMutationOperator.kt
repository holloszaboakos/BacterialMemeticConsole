package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation

sealed interface BacterialMutationOperator {
    operator fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>,
    )
}