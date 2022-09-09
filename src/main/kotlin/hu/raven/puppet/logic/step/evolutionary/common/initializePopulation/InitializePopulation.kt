package hu.raven.puppet.logic.step.evolutionary.common.initializePopulation

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep

sealed class InitializePopulation<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    abstract operator fun invoke()
}