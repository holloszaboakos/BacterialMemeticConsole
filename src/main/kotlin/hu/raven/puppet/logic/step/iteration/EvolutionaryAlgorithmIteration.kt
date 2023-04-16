package hu.raven.puppet.logic.step.iteration

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class EvolutionaryAlgorithmIteration<C : PhysicsUnit<C>>(
    private val steps: Array<EvolutionaryAlgorithmStep<C>>
) : AlgorithmIteration<EvolutionaryAlgorithmState<C>> {

    override operator fun invoke(algorithmState: EvolutionaryAlgorithmState<C>) {
        steps.forEach { step -> step(algorithmState) }
        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().cloneRepresentationAndCost()
            copyOfWorst = population.activesAsSequence().last().cloneRepresentationAndCost()
            iteration++
        }
    }
}