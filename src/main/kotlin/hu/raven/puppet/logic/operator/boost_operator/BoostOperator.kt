package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class BoostOperator<O : OnePartRepresentationWithCost> {
    protected abstract val calculateCostOf: CalculateCost<*>

    abstract operator fun invoke(specimen: O)
}