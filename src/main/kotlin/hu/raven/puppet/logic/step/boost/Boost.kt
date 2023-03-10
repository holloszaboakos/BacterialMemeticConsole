package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class Boost<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    abstract suspend operator fun invoke()
}