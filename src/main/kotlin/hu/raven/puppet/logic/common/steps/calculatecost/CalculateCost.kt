package hu.raven.puppet.logic.common.steps.calculatecost

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

sealed interface CalculateCost {
    operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: AAlgorithm4VRP<S>,
        specimen: ISpecimenRepresentation
    )
}