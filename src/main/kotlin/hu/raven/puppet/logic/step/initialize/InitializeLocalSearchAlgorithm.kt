package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.initializationofiterative.InitializeLocalSearch
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject


class InitializeLocalSearchAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {
    val initializeLocalSearch: InitializeLocalSearch<S, C> by inject()

    override fun invoke() = initializeLocalSearch()
}