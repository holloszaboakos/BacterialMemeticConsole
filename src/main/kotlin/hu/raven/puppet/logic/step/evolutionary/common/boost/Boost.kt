package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep

sealed class Boost<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    abstract suspend operator fun invoke()
}