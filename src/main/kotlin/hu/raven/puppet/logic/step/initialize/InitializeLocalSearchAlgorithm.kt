package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.initializationofiterative.InitializeLocalSearch
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.AlgorithmState


class InitializeLocalSearchAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    val initializeLocalSearch: InitializeLocalSearch<S, C>,
    override val algorithmState: AlgorithmState
) : InitializeAlgorithm<S, C>() {

    override fun invoke() = initializeLocalSearch()
}