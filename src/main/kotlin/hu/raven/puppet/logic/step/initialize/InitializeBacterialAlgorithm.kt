package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class InitializeBacterialAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {
    val initializePopulation: InitializePopulation<S, C> by inject()
    val orderPopulationByCost: OrderPopulationByCost<S, C> by inject()
    val algorithm: IterativeAlgorithmStateWithMultipleCandidates<S, C> by inject()

    override fun invoke() {
        logger("initializePopulation")
        initializePopulation()
        logger("orderByCost")
        runBlocking { orderPopulationByCost() }
        logger("orderedByCost")
        algorithm.apply {
            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
        }
    }
}