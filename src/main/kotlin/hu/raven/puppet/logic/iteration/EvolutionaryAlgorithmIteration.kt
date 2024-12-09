package hu.raven.puppet.logic.iteration

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class EvolutionaryAlgorithmIteration<R, S : EvolutionaryAlgorithmState<R>>(
    private val steps: Array<EvolutionaryAlgorithmStep<R, S>>
) : AlgorithmIteration<S> {

    override operator fun invoke(algorithmState: S) {
        steps.forEach { step -> step(algorithmState) }
        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().copy()
            copyOfWorst = population.activesAsSequence().last().copy()
            iteration++
        }
    }
}