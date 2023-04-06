package hu.raven.puppet.model.statistics

import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.get

class GeneticAlgorithmStatistics<C : PhysicsUnit<C>> {
    val operatorsWithStatistics =
        CrossOverOperator.getVariants<C>(get(), get(), get()).associateWith {
            OperatorStatistics(Fraction.new(0), 1, Fraction.new(Int.MAX_VALUE.toLong()))
        }.toMutableMap()

}