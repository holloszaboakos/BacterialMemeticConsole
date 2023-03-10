package hu.raven.puppet.logic.step

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject

abstract class AlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> {
    protected val logger: DoubleLogger by inject()
    protected val taskHolder: VRPTaskHolder by inject()
    protected val subSolutionFactory: SolutionRepresentationFactory<S, C> by inject()
}