package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject

class MutationOperatorWithSpreadSegment<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
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