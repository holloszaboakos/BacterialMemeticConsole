package hu.raven.puppet.logic.step.localsearchiteration

import hu.raven.puppet.logic.step.IterativeAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class LocalSearchIteration<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : IterativeAlgorithmStep<S, C>() {

    abstract operator fun invoke()
}