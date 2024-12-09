package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


sealed class BoostOperator<R, S : AlgorithmSolution<R, S>> {
    protected abstract val calculateCostOf: CalculateCost<R, *>

    abstract operator fun invoke(specimen: S)
}