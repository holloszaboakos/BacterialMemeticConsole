package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface BoostOperator{
    operator fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>, specimen: S)
}