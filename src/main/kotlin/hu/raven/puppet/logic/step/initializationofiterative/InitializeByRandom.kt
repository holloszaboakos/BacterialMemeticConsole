package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.LocalSearchAlgorithmState
import hu.raven.puppet.utility.extention.toPermutation


class InitializeByRandom<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>,
    val algorithmState: LocalSearchAlgorithmState<C>
) : InitializeLocalSearch<C>() {

    override operator fun invoke(): Unit = algorithmState.run {
        actualCandidate = OnePartRepresentationWithCostAndIteration<C>(
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
        actualCandidate.cost = calculateCostOf(actualCandidate)
    }
}