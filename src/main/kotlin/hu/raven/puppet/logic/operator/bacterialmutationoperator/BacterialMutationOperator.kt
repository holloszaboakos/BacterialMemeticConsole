package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment

sealed class BacterialMutationOperator {

    abstract operator fun invoke(
        clone: OnePartRepresentation,
        selectedSegment: Segment,
    )
}