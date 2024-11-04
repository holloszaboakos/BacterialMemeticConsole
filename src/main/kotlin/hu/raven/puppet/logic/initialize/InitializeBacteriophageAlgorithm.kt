package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.InitializeBacteriophagePopulation
import hu.raven.puppet.model.state.BacteriophageAlgorithmState

class InitializeBacteriophageAlgorithm<T>(
    private val initializeEvolutionaryAlgorithm: InitializeEvolutionaryAlgorithm<T>,
    private val initializePopulation: InitializeBacteriophagePopulation
) : InitializeAlgorithm<T, BacteriophageAlgorithmState<T>> {
    override fun invoke(task: T): BacteriophageAlgorithmState<T> {
        val preState = initializeEvolutionaryAlgorithm(task)
        val bacteriophagePopulation = initializePopulation()
        return BacteriophageAlgorithmState(
            task = preState.task,
            population = preState.population,
            virusPopulation = PoolWithActivation(bacteriophagePopulation).apply { deactivateAll() }
        )
    }
}