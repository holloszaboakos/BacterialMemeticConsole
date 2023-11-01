package hu.raven.puppet.logic.initialize

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.LocalSearchAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.toPermutation


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
                .toPermutation(),
            iterationOfCreation = 0,
            cost = null
        )
        algorithmState.actualCandidate.cost = calculateCostOf(algorithmState.actualCandidate)
        return algorithmState
    }
}