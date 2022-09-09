package hu.raven.puppet.logic.step.localsearch.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.localsearch.LocalSearchStep

sealed class InitializeLocalSearch<S : ISpecimenRepresentation> : LocalSearchStep<S>() {
    abstract operator fun invoke()
}