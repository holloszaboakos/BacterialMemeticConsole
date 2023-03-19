package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation

class OppositionOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {
    override fun invoke(
        clone: S,
        selectSegment: Segment
    ) {
        selectSegment.positions.forEachIndexed { readIndex, writeIndex ->
            clone[writeIndex] = selectSegment.values[selectSegment.values.size - 1 - readIndex]
        }
    }
}