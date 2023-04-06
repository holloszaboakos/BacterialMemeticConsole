package hu.raven.puppet.logic.step

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates


abstract class EvolutionaryAlgorithmStep<C : PhysicsUnit<C>> : AlgorithmStep<C>() {

    abstract override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>
    protected abstract val parameters: EvolutionaryAlgorithmParameterProvider<C>
}