package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking


class InitializeGeneticAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    val initializePopulation: InitializePopulation<S, C>,
    val orderPopulationByCost: OrderPopulationByCost<S, C>,
    val boost: Boost<S, C>,
) : InitializeAlgorithm<S, C>() {

    override fun invoke() {
        initializePopulation()
        runBlocking {
            orderPopulationByCost()
            boost()
        }

        algorithmState.apply {
            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
        }
    }
}