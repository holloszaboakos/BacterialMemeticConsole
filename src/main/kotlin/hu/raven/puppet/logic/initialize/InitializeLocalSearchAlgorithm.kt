package hu.raven.puppet.logic.initialize

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.asPermutation
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.LocalSearchAlgorithmState
import hu.raven.puppet.model.task.Task


class InitializeLocalSearchAlgorithm(
    val calculateCostOf: CalculateCost
) : InitializeAlgorithm<LocalSearchAlgorithmState> {

    override operator fun invoke(task: Task): LocalSearchAlgorithmState {
        val algorithmState = LocalSearchAlgorithmState(
            task = task
        )
        algorithmState.actualCandidate = OnePartRepresentationWithCostAndIteration(
            objectiveCount = task.costGraph.objectives.size,
            permutation = IntArray(
                task.transportUnits.size +
                        task.costGraph.objectives.size
            ) { index ->
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