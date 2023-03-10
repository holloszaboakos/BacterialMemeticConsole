package hu.raven.puppet.logic.step.mutationoperator

import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.asPermutation
import hu.raven.puppet.utility.inject


class MutationOperatorWithContinuesSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {

    val statistics: BacterialAlgorithmStatistics by inject()
    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {

        selectedPositions
            .asPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedElements[readIndex]
            }
    }
}