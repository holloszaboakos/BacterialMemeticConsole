package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.logic.EvolutionaryAlgorithmStepFactory
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class MutateChildrenFactory<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStepFactory<C> {
    abstract override operator fun invoke(): EvolutionaryAlgorithmState<C>.() -> Unit
}