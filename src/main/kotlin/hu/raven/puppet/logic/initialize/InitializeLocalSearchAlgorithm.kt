package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.math.asPermutation
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.LocalSearchAlgorithmState

class InitializeLocalSearchAlgorithm<T>(
    private val calculateCostOf: CalculateCost<T>,
    private val objectiveCount: Int,
    private val permutationSize: Int,
) : InitializeAlgorithm<T, LocalSearchAlgorithmState<T>> {

    override operator fun invoke(task: T): LocalSearchAlgorithmState<T> {
        val algorithmState = LocalSearchAlgorithmState(
            task = task
        )
        algorithmState.actualCandidate = OnePartRepresentationWithCostAndIteration(
            objectiveCount = objectiveCount,
            permutation = IntArray(permutationSize) { index ->
                index
            }
                .apply(IntArray::shuffle)
                .asPermutation(),
            iterationOfCreation = 0,
            cost = null
        )
        algorithmState.actualCandidate.cost = calculateCostOf(algorithmState.actualCandidate)
        return algorithmState
    }
}