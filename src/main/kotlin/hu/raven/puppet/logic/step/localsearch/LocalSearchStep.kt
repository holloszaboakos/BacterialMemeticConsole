package hu.raven.puppet.logic.step.localsearch

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.IterativeAlgorithmState
import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.utility.inject

abstract class LocalSearchStep<S : ISpecimenRepresentation> : AlgorithmStep<S>() {
    protected val algorithmState: IterativeAlgorithmState<S> by inject()
}