package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.logic.operator.initialize_population.InitializePopulation
import hu.raven.puppet.logic.step.order_population_by_cost.OrderPopulationByCost
import hu.raven.puppet.model.state.BasicEvolutionaryAlgorithmState

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask


open class InitializeEvolutionaryAlgorithm<T : AlgorithmTask, R>(
    private val initializePopulation: InitializePopulation<R>,
    private val orderPopulationByCost: OrderPopulationByCost<R, T>
) : InitializeAlgorithm<T, EvolutionaryAlgorithmState<R>> {

    override fun invoke(task: T): EvolutionaryAlgorithmState<R> {
        val population = initializePopulation()
        val algorithmState = BasicEvolutionaryAlgorithmState<R>(
            iteration = 0,
            population = PoolWithActivation(population),
            copyOfBest = null,
            copyOfWorst = null,
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