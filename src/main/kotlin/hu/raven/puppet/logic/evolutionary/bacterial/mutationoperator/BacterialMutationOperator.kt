package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface BacterialMutationOperator<S : ISpecimenRepresentation> {
    val algorithm: BacterialAlgorithm<S>

    operator fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    )
}