package hu.raven.puppet.logic.common.initialize

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface InitializeAlgorithm<S : ISpecimenRepresentation> {
    val algorithm: AAlgorithm4VRP<S>
    operator fun invoke()
}