package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.logic.operator.initialize_virus_population.InitializeVirusPopulation

import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState

class InitializeVirusEvolutionaryAlgorithm<T>(
    private val initializeEvolutionaryAlgorithm: InitializeEvolutionaryAlgorithm<T>,
    private val initializeVirusPopulation: InitializeVirusPopulation
) : InitializeAlgorithm<T, VirusEvolutionaryAlgorithmState<T>> {
    override fun invoke(task: T): VirusEvolutionaryAlgorithmState<T> {
        val preState = initializeEvolutionaryAlgorithm(task)
        val permutations = preState.population.activesAsSequence()
            .map { it.value.permutation }
            .toList()
        val virusPopulation = initializeVirusPopulation(permutations)
        return VirusEvolutionaryAlgorithmState(
            task = preState.task,
            population = preState.population,
            virusPopulation = PoolWithActivation(virusPopulation)
        )
    }
}