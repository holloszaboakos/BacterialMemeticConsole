package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class InitializeGeneticAlgorithm<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val initializePopulation: InitializePopulation<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>,
    val boost: Boost<C>,
) : InitializeAlgorithm<C>() {

    override fun invoke() {
        initializePopulation()
        orderPopulationByCost(algorithmState)
        boost(algorithmState)

        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().copy()
            copyOfWorst = population.activesAsSequence().last().copy()
        }
    }
}