package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class InitializeGeneticAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {
    val algorithm: IterativeAlgorithmStateWithMultipleCandidates<S, C> by inject()
    val initializePopulation: InitializePopulation<S, C> by inject()
    val orderPopulationByCost: OrderPopulationByCost<S, C> by inject()
    val boost: Boost<S, C> by inject()

    override fun invoke() {
        initializePopulation()
        runBlocking {
            orderPopulationByCost()
            boost()
        }

        algorithm.apply {
            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
        }
    }
}