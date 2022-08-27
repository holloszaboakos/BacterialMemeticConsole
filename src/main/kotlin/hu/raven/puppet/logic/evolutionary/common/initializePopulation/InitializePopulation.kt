package hu.raven.puppet.logic.evolutionary.common.initializePopulation

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface InitializePopulation {
    operator fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>)
}