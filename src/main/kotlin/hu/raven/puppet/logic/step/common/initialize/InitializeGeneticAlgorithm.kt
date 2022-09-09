package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class InitializeGeneticAlgorithm<S : ISpecimenRepresentation> : InitializeAlgorithm<S>() {
    val algorithm: EvolutionaryAlgorithmState<S> by inject()
    val initializePopulation: InitializePopulation<S> by inject()
    val orderPopulationByCost: OrderPopulationByCost<S> by inject()
    val boost: Boost<S> by inject()

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