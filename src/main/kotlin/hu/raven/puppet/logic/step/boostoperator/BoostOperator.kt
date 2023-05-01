package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class BoostOperator<O : OnePartRepresentationWithCost> {
    abstract val calculateCostOf: CalculateCost

    abstract operator fun invoke(specimen: O)
}