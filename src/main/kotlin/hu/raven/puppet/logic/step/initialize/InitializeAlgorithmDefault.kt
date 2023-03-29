package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory

class InitializeAlgorithmDefault<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>
) : InitializeAlgorithm<S, C>() {

    override operator fun invoke() {}
}