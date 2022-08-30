package hu.raven.puppet.logic.evolutionary.common.iteration

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface EvolutionaryIteration<S : ISpecimenRepresentation> {
    val algorithm: SEvolutionaryAlgorithm<S>
    operator fun invoke(
        manageLifeCycle: Boolean
    )
}