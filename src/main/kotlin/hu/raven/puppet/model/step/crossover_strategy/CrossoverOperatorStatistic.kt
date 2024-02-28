package hu.raven.puppet.model.step.crossover_strategy

import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator

class CrossoverOperatorStatistic(
    variants: List<CrossOverOperator>
) {
    val operatorsWithStatistics =
        variants
            .associateWith {
                OperatorStatistics(0f, 1, Float.MAX_VALUE)
            }
            .toMutableMap()

}