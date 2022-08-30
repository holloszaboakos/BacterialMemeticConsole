package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface BacterialMutationOperator {
    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    )
}