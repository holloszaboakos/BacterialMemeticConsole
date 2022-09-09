package hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep

sealed class MutateChildren<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    abstract operator fun invoke()
}