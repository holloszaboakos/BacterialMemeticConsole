package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.utility.inject


class InitializeLocalSearchAlgorithm<S : ISpecimenRepresentation> : InitializeAlgorithm<S>() {
    val initializeLocalSearch: InitializeLocalSearch<S> by inject()

    override fun invoke() = initializeLocalSearch()
}