package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface Boost {
    suspend operator fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>)
}