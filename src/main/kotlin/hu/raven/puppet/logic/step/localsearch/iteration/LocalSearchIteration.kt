package hu.raven.puppet.logic.step.localsearch.iteration

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.localsearch.LocalSearchStep

sealed class LocalSearchIteration<S : ISpecimenRepresentation> : LocalSearchStep<S>() {

    abstract operator fun invoke()
}