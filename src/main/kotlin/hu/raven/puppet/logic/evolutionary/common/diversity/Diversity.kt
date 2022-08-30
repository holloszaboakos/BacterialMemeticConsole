package hu.raven.puppet.logic.evolutionary.common.diversity

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface Diversity<S : ISpecimenRepresentation> {
    val algorithm: SEvolutionaryAlgorithm<S>
    operator fun invoke()
}