package hu.raven.puppet.logic.step.mutationoperator

import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject

class MutationOperatorWithSpreadSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {

    val statistics: BacterialAlgorithmStatistics by inject()

    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {

        val shuffler = (0 until cloneSegmentLength).shuffled()
        selectedPositions.forEachIndexed { index, position ->
            clone[position] = selectedElements[shuffler[index]]
        }
    }
}