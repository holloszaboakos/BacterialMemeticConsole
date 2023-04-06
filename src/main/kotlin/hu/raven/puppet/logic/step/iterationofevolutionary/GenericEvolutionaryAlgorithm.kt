package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.EvolutionaryAlgorithmStepFactory
import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GenericEvolutionaryAlgorithm<C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    val algorithmState: EvolutionaryAlgorithmState<C>,
    stepFactories: Array<EvolutionaryAlgorithmStepFactory<C>>,
) : EvolutionaryIteration<C>() {

    /*
    * GENETIC
    * order
    * boost
    * select
    * crossover
    * mutate
    * */

    private val steps = stepFactories
        .map(EvolutionaryAlgorithmStepFactory<C>::invoke)
        .toTypedArray()

    override fun invoke() {
        steps.forEach { step -> algorithmState.step() }
        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
            iteration++
        }
    }
}