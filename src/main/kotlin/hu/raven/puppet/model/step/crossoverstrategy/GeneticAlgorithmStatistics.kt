package hu.raven.puppet.model.step.crossoverstrategy

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.task.CostGraph

class GeneticAlgorithmStatistics(val costGraph: CostGraph) {
    val operatorsWithStatistics =
        CrossOverOperator.getVariants(costGraph).associateWith {
            OperatorStatistics(Fraction.new(0), 1, Fraction.new(Int.MAX_VALUE.toLong()))
        }.toMutableMap()

}