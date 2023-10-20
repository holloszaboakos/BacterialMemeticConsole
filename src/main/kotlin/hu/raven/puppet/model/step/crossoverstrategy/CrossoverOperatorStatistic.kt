package hu.raven.puppet.model.step.crossoverstrategy

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.task.CostGraph

class CrossoverOperatorStatistic(
    val costGraph: CostGraph,
    variants: List<CrossOverOperator>
) {
    val operatorsWithStatistics =
        variants
            .associateWith {
                OperatorStatistics(0f, 1, Float.MAX_VALUE)
            }
            .toMutableMap()

}