package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface BoostOperator<S : ISpecimenRepresentation> {
    val algorithm: SEvolutionaryAlgorithm<S>
    operator fun invoke(specimen: S)
}