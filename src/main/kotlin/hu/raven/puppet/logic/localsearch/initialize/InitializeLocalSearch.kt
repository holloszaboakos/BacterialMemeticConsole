package hu.raven.puppet.logic.localsearch.initialize

import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface InitializeLocalSearch<S : ISpecimenRepresentation> {
    val algorithm: SLocalSearch<S>

    operator fun invoke(
    )
}