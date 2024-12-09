package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.PoolWithActivation
import hu.raven.puppet.logic.operator.initialize_virus_population.InitializeVirusPopulation
import hu.raven.puppet.model.state.VirusAlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask

class InitializeVirusEvolutionaryAlgorithm<T : AlgorithmTask, R>(
    private val initializeEvolutionaryAlgorithm: InitializeEvolutionaryAlgorithm<T, R>,
    private val initializeVirusPopulation: InitializeVirusPopulation<R>
) : InitializeAlgorithm<T, VirusAlgorithmState<R>> {
    override fun invoke(task: T): VirusAlgorithmState<R> {
        val preState = initializeEvolutionaryAlgorithm(task)
        val permutations = preState.population.activesAsSequence()
            .map { it.value.representation }
            .toList()
        val virusPopulation = initializeVirusPopulation(permutations)
        return VirusAlgorithmState(
            iteration = 0,
            population = preState.population,
            virusPopulation = PoolWithActivation(virusPopulation),
            copyOfWorst = null,
            copyOfBest = null,
        )
    }
}