package hu.raven.puppet.logic.step

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates


abstract class EvolutionaryAlgorithmStep<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {

    abstract override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>
    protected abstract val parameters: EvolutionaryAlgorithmParameterProvider<S, C>
}