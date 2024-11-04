package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


open class InitializeEvolutionaryAlgorithm<T>(
    private val initializePopulation: InitializePopulation,
    private val orderPopulationByCost: OrderPopulationByCost<T>
) : InitializeAlgorithm<T, EvolutionaryAlgorithmState<T>> {

    override fun invoke(task: T): EvolutionaryAlgorithmState<T> {
        val population = initializePopulation()
        val algorithmState = EvolutionaryAlgorithmState(
            task = task,
            population = PoolWithActivation(population)
        )
        algorithmState.population.activateAll()
        orderPopulationByCost(algorithmState)
        algorithmState.apply {
            copyOfBest = algorithmState.population.activesAsSequence().first().copy()
            copyOfWorst = algorithmState.population.activesAsSequence().last().copy()
        }
        return algorithmState
    }
}