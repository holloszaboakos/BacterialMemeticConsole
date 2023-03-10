package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class InitializeAlgorithmDefault<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {

    override operator fun invoke() {}
}