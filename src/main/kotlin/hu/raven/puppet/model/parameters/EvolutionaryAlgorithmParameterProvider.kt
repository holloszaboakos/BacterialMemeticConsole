package hu.raven.puppet.model.parameters

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

open class EvolutionaryAlgorithmParameterProvider(
    override val algorithmState: EvolutionaryAlgorithmState,
    iterationLimit: Int,
    val sizeOfPopulation: Int,
    val geneCount: Int,
) : IterativeAlgorithmParameterProvider(algorithmState, iterationLimit)