package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GenericEvolutionaryAlgorithm<C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    val algorithmState: EvolutionaryAlgorithmState<C>,
    steps: Array<EvolutionaryAlgorithmStep<C>>
) : EvolutionaryIteration<C>() {

    /*
    * GENETIC
    * order
    * boost
    * select
    * crossover
    * mutate
    * */

    private val steps = steps

    override fun invoke() {
        steps.forEach { step -> step(algorithmState) }
        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().copy()
            copyOfWorst = population.activesAsSequence().last().copy()
            iteration++
        }
    }
}