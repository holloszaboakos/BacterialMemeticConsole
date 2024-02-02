package hu.raven.puppet.logic.initialize

import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task


open class InitializeEvolutionaryAlgorithm(
    val initializePopulation: InitializePopulation,
    val orderPopulationByCost: OrderPopulationByCost
) : InitializeAlgorithm<EvolutionaryAlgorithmState> {

    override fun invoke(task: Task): EvolutionaryAlgorithmState {
        val population = initializePopulation(task)
        val algorithmState = EvolutionaryAlgorithmState(
            task = task,
            population = PoolWithSmartActivation(population)
        )
        algorithmState.population.activateAll()
        orderPopulationByCost(algorithmState)
        algorithmState.apply {
            copyOfBest = algorithmState.population.activesAsSequence().first().cloneRepresentationAndCost()
            copyOfWorst = algorithmState.population.activesAsSequence().last().cloneRepresentationAndCost()
        }
        return algorithmState
    }
}