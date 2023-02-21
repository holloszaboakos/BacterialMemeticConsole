package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.shuffled
import hu.raven.puppet.utility.inject


class MutationOperatorWithContinuesSegment<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {

    val statistics: BacterialAlgorithmStatistics by inject()
    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {

        selectedPositions
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedElements[readIndex]
            }
    }
}