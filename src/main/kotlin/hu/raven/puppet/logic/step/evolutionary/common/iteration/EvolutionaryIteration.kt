package hu.raven.puppet.logic.step.evolutionary.common.iteration

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep

sealed class EvolutionaryIteration<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    abstract operator fun invoke()
}