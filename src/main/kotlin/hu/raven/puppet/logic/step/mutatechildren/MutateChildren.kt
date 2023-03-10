package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class MutateChildren<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    abstract operator fun invoke()
}