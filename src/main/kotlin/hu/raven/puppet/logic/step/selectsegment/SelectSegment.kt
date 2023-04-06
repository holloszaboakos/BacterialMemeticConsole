package hu.raven.puppet.logic.step.selectsegment

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment

sealed class SelectSegment<C : PhysicsUnit<C>> {
    abstract val parameters: BacterialMutationParameterProvider<C>

    abstract operator fun invoke(
        specimen: OnePartRepresentation<C>,
        cycleIndex: Int,
        cycleCount: Int
    ): Segment
}