package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

open class EvolutionaryAlgorithmParameterProvider<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    iterationLimit: Int,
    val sizeOfPopulation: Int,
    val geneCount: Int
) : IterativeAlgorithmParameterProvider(algorithmState, iterationLimit)