package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.boost.BoostFactory
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCostFactory
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class InitializeGeneticAlgorithm<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val initializePopulation: InitializePopulation<C>,
    val orderPopulationByCost: OrderPopulationByCostFactory<C>,
    val boost: BoostFactory<C>,
) : InitializeAlgorithm<C>() {

    override fun invoke() {
        initializePopulation()
        orderPopulationByCost()(algorithmState)
        boost()(algorithmState)

        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
        }
    }
}