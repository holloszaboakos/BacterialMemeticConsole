package hu.raven.puppet.logic.evolutionary.common.initializePopulation

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface InitializePopulation<S : ISpecimenRepresentation> {
    val algorithm: SEvolutionaryAlgorithm<S>
    operator fun invoke()
}