package hu.raven.puppet.logic.step.localsearch.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.utility.inject


class InitializeByRandom<S : ISpecimenRepresentation> : InitializeLocalSearch<S>() {
    val calculateCostOf: CalculateCost<S> by inject()

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