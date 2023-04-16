package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task


class InitializeEvolutionaryAlgorithm<C : PhysicsUnit<C>>(
    val initializePopulation: InitializePopulation<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>
) : InitializeAlgorithm<EvolutionaryAlgorithmState<C>>() {

    override fun invoke(task: Task): EvolutionaryAlgorithmState<C> {
        val population = initializePopulation(task)
        val algorithmState = EvolutionaryAlgorithmState(
            task = task,
            population = PoolWithSmartActivation(population)
        )
        orderPopulationByCost(algorithmState)
        algorithmState.apply {
            copyOfBest = algorithmState.population.activesAsSequence().first().cloneRepresentationAndCost()
            copyOfWorst = algorithmState.population.activesAsSequence().last().cloneRepresentationAndCost()
        }
        return algorithmState
    }
}