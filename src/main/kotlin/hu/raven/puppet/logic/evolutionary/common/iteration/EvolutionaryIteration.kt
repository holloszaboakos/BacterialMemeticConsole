package hu.raven.puppet.logic.evolutionary.common.iteration

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface EvolutionaryIteration {
    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SEvolutionaryAlgorithm<S>,
        manageLifeCycle: Boolean
    )
}