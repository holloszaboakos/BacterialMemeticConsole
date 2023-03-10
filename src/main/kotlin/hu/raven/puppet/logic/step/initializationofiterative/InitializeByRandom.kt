package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject


class InitializeByRandom<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : InitializeLocalSearch<S, C>() {
    val calculateCostOf: CalculateCost<S, C> by inject()

    override operator fun invoke() = algorithmState.run {
        actualCandidate = subSolutionFactory.produce(0, Array(taskHolder.task.salesmen.size) { index ->
            if (index == 0)
                IntArray(taskHolder.task.costGraph.objectives.size) { it }
            else
                intArrayOf()
        }).apply {
            shuffle()
        }
        calculateCostOf(actualCandidate)
    }
}