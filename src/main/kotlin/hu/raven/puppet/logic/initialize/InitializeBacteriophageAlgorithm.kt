package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.InitializeBacteriophagePopulation
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask

class InitializeBacteriophageAlgorithm<T : AlgorithmTask, R>(
    private val initializeEvolutionaryAlgorithm: InitializeEvolutionaryAlgorithm<T, R>,
    private val initializePopulation: InitializeBacteriophagePopulation
) : InitializeAlgorithm<T, BacteriophageAlgorithmState<R>> {
    override fun invoke(task: T): BacteriophageAlgorithmState<R> {
        val preState = initializeEvolutionaryAlgorithm(task)
        val bacteriophagePopulation = initializePopulation()

        return BacteriophageAlgorithmState(
            iteration = 0,
            population = preState.population,
            virusPopulation = PoolWithActivation(bacteriophagePopulation).apply { deactivateAll() },
            copyOfBest = null,
            copyOfWorst = null,
        )
    }
}