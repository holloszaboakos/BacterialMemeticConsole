package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.AlgorithmState
import hu.raven.puppet.model.state.LocalSearchAlgorithmState


class InitializeByRandom<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>,
    val algorithmState: LocalSearchAlgorithmState<C>
) : InitializeLocalSearch<C>() {

    override operator fun invoke() = algorithmState.run {
        actualCandidate = OnePartRepresentation<C>(0, Array(task.transportUnits.size) { index ->
            if (index == 0)
                IntArray(task.costGraph.objectives.size) { it }
            else
                intArrayOf()
        }).apply {
            shuffle()
        }
        calculateCostOf(actualCandidate)
    }
}