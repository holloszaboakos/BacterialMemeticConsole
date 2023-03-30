package hu.raven.puppet.logic.step

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.AlgorithmState

abstract class AlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> {
    protected abstract val algorithmState: AlgorithmState
    protected abstract val logger: DoubleLogger
    protected abstract val subSolutionFactory: SolutionRepresentationFactory<S, C>
}