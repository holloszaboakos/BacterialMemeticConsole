package hu.raven.puppet.logic.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface MutateChildren<S : ISpecimenRepresentation> {
    val algorithm: GeneticAlgorithm<S>

    operator fun invoke()
}