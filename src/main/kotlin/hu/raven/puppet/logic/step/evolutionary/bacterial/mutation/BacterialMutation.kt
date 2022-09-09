package hu.raven.puppet.logic.step.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep

sealed class BacterialMutation<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {

    abstract suspend operator fun invoke()
}