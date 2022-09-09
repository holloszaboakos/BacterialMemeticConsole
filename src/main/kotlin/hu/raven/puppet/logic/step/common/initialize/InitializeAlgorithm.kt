package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.AlgorithmStep

sealed class InitializeAlgorithm<S : ISpecimenRepresentation> : AlgorithmStep<S>() {
    abstract operator fun invoke()
}