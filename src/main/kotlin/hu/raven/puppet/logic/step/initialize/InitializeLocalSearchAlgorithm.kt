package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.initializationofiterative.InitializeLocalSearch
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.AlgorithmState


class InitializeLocalSearchAlgorithm<C : PhysicsUnit<C>>(
    val initializeLocalSearch: InitializeLocalSearch<C>,
    override val algorithmState: AlgorithmState
) : InitializeAlgorithm<C>() {

    override fun invoke() = initializeLocalSearch()
}