package hu.raven.puppet.logic.step.initializationofiterative

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory


class InitializeByRandom<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    val calculateCostOf: CalculateCost<S, C>
) : InitializeLocalSearch<S, C>() {

    override operator fun invoke() = algorithmState.run {
        actualCandidate = subSolutionFactory.produce(0, Array(task.transportUnits.size) { index ->
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