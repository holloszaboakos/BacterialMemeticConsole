package hu.raven.puppet.logic.iteration

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class EvolutionaryAlgorithmIteration<T : EvolutionaryAlgorithmState<*>>(
    private val steps: Array<EvolutionaryAlgorithmStep<T>>
) : AlgorithmIteration<T> {

    override operator fun invoke(algorithmState: T) {
        steps.forEach { step -> step(algorithmState) }
        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().copy()
            copyOfWorst = population.activesAsSequence().last().copy()
            iteration++
        }
    }
}