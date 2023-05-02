package hu.raven.puppet.model.statistics

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneticAlgorithmStatistics(val state: EvolutionaryAlgorithmState) {
    val operatorsWithStatistics =
        CrossOverOperator.getVariants(state.task.costGraph).associateWith {
            OperatorStatistics(Fraction.new(0), 1, Fraction.new(Int.MAX_VALUE.toLong()))
        }.toMutableMap()

}