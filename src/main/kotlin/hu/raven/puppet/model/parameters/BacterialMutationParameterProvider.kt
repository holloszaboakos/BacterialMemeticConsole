package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class BacterialMutationParameterProvider(
    algorithmState: EvolutionaryAlgorithmState,
    iterationLimit: Int,
    sizeOfPopulation: Int,
    geneCount: Int,
    val cloneCount: Int,
    val cloneSegmentLength: Int,
    val cloneCycleCount: Int,
    val mutationPercentage: Float,
) : EvolutionaryAlgorithmParameterProvider(
    algorithmState, iterationLimit, sizeOfPopulation, geneCount
)