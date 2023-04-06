package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class BacterialMutationParameterProvider<C : PhysicsUnit<C>>(
    algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    iterationLimit: Int,
    sizeOfPopulation: Int,
    geneCount: Int,
    val cloneCount: Int,
    val cloneSegmentLength: Int,
    val cloneCycleCount: Int,
    val mutationPercentage: Float,
) : EvolutionaryAlgorithmParameterProvider<C>(
    algorithmState, iterationLimit, sizeOfPopulation, geneCount
)