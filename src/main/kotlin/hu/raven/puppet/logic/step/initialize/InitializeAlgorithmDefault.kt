package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class InitializeAlgorithmDefault<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {

    override operator fun invoke() {}
}