package hu.raven.puppet.logic.initialize

import hu.raven.puppet.logic.operator.initialize_bacteriophage_population.InitializeBacteriophagePopulation
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.task.Task

class InitializeBacteriophageAlgorithm(
    val initializeEvolutionaryAlgorithm: InitializeEvolutionaryAlgorithm,
    val initializePopulation: InitializeBacteriophagePopulation
) : InitializeAlgorithm<BacteriophageAlgorithmState> {
    override fun invoke(task: Task): BacteriophageAlgorithmState {
        val preState = initializeEvolutionaryAlgorithm(task)
        val bacteriophagePopulation = initializePopulation()
        return BacteriophageAlgorithmState(
            task = preState.task,
            population = preState.population,
            virusPopulation = PoolWithSmartActivation(bacteriophagePopulation).apply { deactivateAll() }
        )
    }
}