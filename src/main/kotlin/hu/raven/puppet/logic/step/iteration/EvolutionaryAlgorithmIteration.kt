package hu.raven.puppet.logic.step.iteration

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class EvolutionaryAlgorithmIteration(
    private val steps: Array<EvolutionaryAlgorithmStep>
) : AlgorithmIteration<EvolutionaryAlgorithmState> {

    override operator fun invoke(algorithmState: EvolutionaryAlgorithmState) {
        steps.forEach { step -> step(algorithmState) }
        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().cloneRepresentationAndCost()
            copyOfWorst = population.activesAsSequence().last().cloneRepresentationAndCost()
            iteration++
        }
    }
}