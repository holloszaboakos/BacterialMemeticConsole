package hu.raven.puppet.logic.initialize

import hu.raven.puppet.logic.operator.initialize_virus_population.InitializeVirusPopulation
import hu.raven.puppet.model.solution.PoolWithSmartActivation
import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task

class InitializeVirusEvolutionaryAlgorithm(
    val initializeEvolutionaryAlgorithm: InitializeEvolutionaryAlgorithm,
    val initializeVirusPopulation: InitializeVirusPopulation
) : InitializeAlgorithm<VirusEvolutionaryAlgorithmState> {
    override fun invoke(task: Task): VirusEvolutionaryAlgorithmState {
        val preState = initializeEvolutionaryAlgorithm(task)
        val permutations = preState.population.activesAsSequence()
            .map { it.permutation }
            .toList()
        val virusPopulation = initializeVirusPopulation(permutations)
        return VirusEvolutionaryAlgorithmState(
            task = preState.task,
            population = preState.population,
            virusPopulation = PoolWithSmartActivation(virusPopulation)
        )
    }
}