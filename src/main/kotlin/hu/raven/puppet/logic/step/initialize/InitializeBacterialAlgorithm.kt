package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class InitializeBacterialAlgorithm<C : PhysicsUnit<C>>(
    val initializePopulation: InitializePopulation<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>,
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val logger: DoubleLogger,
) : InitializeAlgorithm<C>() {

    override fun invoke() {
        logger("initializePopulation")
        initializePopulation()
        logger("orderByCost")
        orderPopulationByCost(algorithmState)
        logger("orderedByCost")
        algorithmState.apply {
            copyOfBest = population.activesAsSequence().first().clone()
            copyOfWorst = population.activesAsSequence().last().clone()
        }
    }
}