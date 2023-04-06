package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.runBlocking


class InitializeGeneticAlgorithm<C : PhysicsUnit<C>>(
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    val initializePopulation: InitializePopulation<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>,
    val boost: Boost<C>,
) : InitializeAlgorithm<C>() {

    override fun invoke() {
        initializePopulation()
        runBlocking {
            orderPopulationByCost()
            boost()
        }

        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
        }
    }
}