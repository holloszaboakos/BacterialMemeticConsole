package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface InitializeAlgorithm {
    operator fun <S : ISpecimenRepresentation> invoke(algorithm: AAlgorithm4VRP<S>)
}