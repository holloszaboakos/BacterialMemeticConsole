package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

open class EvolutionaryAlgorithmParameterProvider<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    iterationLimit: Int,
    val sizeOfPopulation: Int,
    val geneCount: Int,
) : IterativeAlgorithmParameterProvider(algorithmState, iterationLimit)