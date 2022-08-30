package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface Boost<S : ISpecimenRepresentation> {
    val algorithm: SEvolutionaryAlgorithm<S>
    suspend operator fun invoke()
}