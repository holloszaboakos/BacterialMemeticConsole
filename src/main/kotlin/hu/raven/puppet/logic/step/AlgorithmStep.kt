package hu.raven.puppet.logic.step

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory

abstract class AlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> {
    protected abstract val logger: DoubleLogger
    protected abstract val taskHolder: VRPTaskHolder
    protected abstract val subSolutionFactory: SolutionRepresentationFactory<S, C>
}