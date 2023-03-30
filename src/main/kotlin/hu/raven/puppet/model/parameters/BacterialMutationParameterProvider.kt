package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class BacterialMutationParameterProvider<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    iterationLimit: Int,
    sizeOfPopulation: Int,
    geneCount: Int,
    val cloneCount: Int,
    val cloneSegmentLength: Int,
    val cloneCycleCount: Int,
    val mutationPercentage: Float,
) : EvolutionaryAlgorithmParameterProvider<S, C>(
    algorithmState, iterationLimit, sizeOfPopulation, geneCount
)