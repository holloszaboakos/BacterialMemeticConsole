package hu.raven.puppet.logic.localsearch.initialize

import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface InitializeLocalSearch {
    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SLocalSearch<S>
    )
}