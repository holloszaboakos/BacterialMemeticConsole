package hu.raven.puppet.logic.step

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.AlgorithmState

abstract class AlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> {
    protected abstract val algorithmState: AlgorithmState
    protected abstract val solutionFactory: SolutionRepresentationFactory<S, C>
}