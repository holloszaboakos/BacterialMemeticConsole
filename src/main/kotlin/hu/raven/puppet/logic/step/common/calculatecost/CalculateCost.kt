package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.AlgorithmStep

sealed class CalculateCost<S : ISpecimenRepresentation> : AlgorithmStep<S>() {
    abstract operator fun invoke(
        specimen: ISpecimenRepresentation
    )
}