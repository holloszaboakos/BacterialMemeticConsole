package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation

sealed class BacterialMutationOperator {
    abstract operator fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>,
    )
}