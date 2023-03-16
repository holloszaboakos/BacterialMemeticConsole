package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.asPermutation


class RandomShuffleOfContinuesSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {
    override fun invoke(
        clone: S,
        selectSegment: Segment
    ) {
        selectSegment.positions
            .asPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectSegment.values[readIndex]
            }
    }
}