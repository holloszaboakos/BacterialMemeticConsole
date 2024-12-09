package hu.raven.puppet.logic.initialize

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.LocalSearchState
import hu.raven.puppet.model.task.AlgorithmTask

class InitializeLocalSearchAlgorithm<T : AlgorithmTask, R>(
    private val calculateCostOf: CalculateCost<R, T>,
    private val objectiveCount: Int,
    private val produceInitialRepresentation: () -> R,
) : InitializeAlgorithm<T, LocalSearchState<R, SolutionWithIteration<R>>> {

    override operator fun invoke(task: T): LocalSearchState<R, SolutionWithIteration<R>> {
        val actualCandidate = SolutionWithIteration(
            representation = produceInitialRepresentation(),
            iterationOfCreation = 0,
            cost = null
        )
        val algorithmState = LocalSearchState(
            iteration = 0,
            candidateSolution = actualCandidate,
        )
        algorithmState.candidateSolution.cost = calculateCostOf(algorithmState.candidateSolution.representation)
        return algorithmState
    }
}