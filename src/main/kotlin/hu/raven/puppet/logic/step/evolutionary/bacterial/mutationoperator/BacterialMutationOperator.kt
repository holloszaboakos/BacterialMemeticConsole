package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject

sealed class BacterialMutationOperator<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {
    val cloneSegmentLength: Int by inject(AlgorithmParameters.CLONE_SEGMENT_LENGTH)

    abstract operator fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    )
}