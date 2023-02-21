package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class InitializeGeneticAlgorithm<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : InitializeAlgorithm<S, C>() {
    val algorithm: EvolutionaryAlgorithmState<S, C> by inject()
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