package hu.raven.puppet.logic.step.initialize

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.LocalSearchAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.toPermutation


class InitializeLocalSearchAlgorithm<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>
) : InitializeAlgorithm<LocalSearchAlgorithmState<C>>() {

    override operator fun invoke(task: Task): LocalSearchAlgorithmState<C> {
        val algorithmState = LocalSearchAlgorithmState<C>(
            task = task
        )
        algorithmState.actualCandidate = OnePartRepresentationWithCostAndIteration<C>(
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