package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation

sealed interface BacterialMutationOperator {
    operator fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>,
    )
}