package hu.raven.puppet.logic.initialize

import hu.raven.puppet.logic.operator.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task


class InitializeEvolutionaryAlgorithm(
    val initializePopulation: InitializePopulation,
    val orderPopulationByCost: OrderPopulationByCost
) : InitializeAlgorithm<EvolutionaryAlgorithmState>() {

    override fun invoke(task: Task): EvolutionaryAlgorithmState {
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