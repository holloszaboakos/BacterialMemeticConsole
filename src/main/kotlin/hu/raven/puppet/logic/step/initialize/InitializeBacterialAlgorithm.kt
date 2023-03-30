package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking


class InitializeBacterialAlgorithm<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    val initializePopulation: InitializePopulation<S, C>,
    val orderPopulationByCost: OrderPopulationByCost<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    val logger: DoubleLogger,
) : InitializeAlgorithm<S, C>() {

    override fun invoke() {
        logger("initializePopulation")
        initializePopulation()
        logger("orderByCost")
        runBlocking { orderPopulationByCost() }
        logger("orderedByCost")
        algorithmState.apply {
            copyOfBest = solutionFactory.copy(population.first())
            copyOfWorst = solutionFactory.copy(population.last())
        }
    }
}