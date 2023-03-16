package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class RandomShuffleOfSpreadSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {

    override fun invoke(
        clone: S,
        selectedSegment: Segment
    ) {

        val shuffler = (0 until cloneSegmentLength).shuffled()
        selectedSegment.positions.forEachIndexed { index, position ->
            clone[position] = selectedSegment.values[shuffler[index]]
        }
    }
}