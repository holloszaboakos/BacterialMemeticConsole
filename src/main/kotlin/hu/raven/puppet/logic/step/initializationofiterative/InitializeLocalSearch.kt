package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.logic.step.IterativeAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class InitializeLocalSearch<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : IterativeAlgorithmStep<S, C>() {
    abstract operator fun invoke()
}