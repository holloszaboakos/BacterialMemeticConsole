package hu.raven.puppet.logic.localsearch.iteration

import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface LocalSearchIteration {
    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SLocalSearch<S>
    )
}