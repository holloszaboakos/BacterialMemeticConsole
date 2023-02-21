package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.localsearch.initialize.InitializeLocalSearch
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject


class InitializeLocalSearchAlgorithm<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {
    val initializeLocalSearch: InitializeLocalSearch<S, C> by inject()

    override fun invoke() = initializeLocalSearch()
}