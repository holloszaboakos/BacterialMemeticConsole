package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class BoostOperator<O : OnePartRepresentationWithCost> {
    protected abstract val calculateCostOf: CalculateCost

    abstract operator fun invoke(specimen: O)
}