package hu.raven.puppet.logic.common.steps.calculatecost

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface CalculateCost<S : ISpecimenRepresentation> {
    val algorithm: AAlgorithm4VRP<S>

    operator fun invoke(
        specimen: ISpecimenRepresentation
    )
}