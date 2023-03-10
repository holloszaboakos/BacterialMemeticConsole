package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class InitializeAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    abstract operator fun invoke()
}