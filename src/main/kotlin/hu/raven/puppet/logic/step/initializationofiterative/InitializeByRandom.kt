package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.LocalSearchAlgorithmState


class InitializeByRandom<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>,
    val algorithmState: LocalSearchAlgorithmState<C>
) : InitializeLocalSearch<C>() {

    override operator fun invoke() = algorithmState.run {
        actualCandidate = OnePartRepresentation<C>(
            id = 0,
            objectiveCount = task.costGraph.objectives.size,
            permutation = Permutation(IntArray(
                task.transportUnits.size +
                        task.costGraph.objectives.size
            ) { index ->
                index
            })
        ).apply {
            permutation.shuffle()
        }
        calculateCostOf(actualCandidate)
    }
}