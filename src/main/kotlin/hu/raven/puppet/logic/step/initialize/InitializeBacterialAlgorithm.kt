package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking


class InitializeBacterialAlgorithm<C : PhysicsUnit<C>>(
    val initializePopulation: InitializePopulation<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    val logger: DoubleLogger,
) : InitializeAlgorithm<C>() {

    override fun invoke() {
        logger("initializePopulation")
        initializePopulation()
        logger("orderByCost")
        runBlocking { orderPopulationByCost() }
        logger("orderedByCost")
        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
        }
    }
}