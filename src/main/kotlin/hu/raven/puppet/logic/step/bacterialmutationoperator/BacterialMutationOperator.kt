package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.solution.Segment

sealed class BacterialMutationOperator<C : PhysicsUnit<C>> {

    abstract operator fun invoke(
        clone: PoolItem<OnePartRepresentationWithIteration<C>>,
        selectedSegment: Segment,
    )
}