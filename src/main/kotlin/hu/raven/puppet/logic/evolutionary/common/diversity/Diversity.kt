package hu.raven.puppet.logic.evolutionary.common.diversity

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface Diversity{
    operator fun<S: ISpecimenRepresentation> invoke(algorithm:SEvolutionaryAlgorithm<S>)
}