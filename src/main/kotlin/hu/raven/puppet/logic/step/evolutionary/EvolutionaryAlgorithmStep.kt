package hu.raven.puppet.logic.step.evolutionary

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject


abstract class EvolutionaryAlgorithmStep<S : ISpecimenRepresentation> : AlgorithmStep<S>() {
    protected val algorithmState: EvolutionaryAlgorithmState<S> by inject()

    protected val sizeOfPopulation: Int by inject(AlgorithmParameters.SIZE_OF_POPULATION)
    protected val iterationLimit: Int by inject(AlgorithmParameters.ITERATION_LIMIT)
    protected val geneCount by lazy { algorithmState.population.first().permutationSize }
}