package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class InitializeAlgorithmDefault<S : ISpecimenRepresentation> : InitializeAlgorithm<S>() {

    override operator fun invoke() {}
}