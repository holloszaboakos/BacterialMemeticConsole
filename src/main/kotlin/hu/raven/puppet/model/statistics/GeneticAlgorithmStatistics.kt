package hu.raven.puppet.model.statistics

import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class GeneticAlgorithmStatistics<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStatistics {
    override var diversity = Double.MAX_VALUE
    val operatorsWithStatistics =
        CrossOverOperator.getVariants<S, C>().associateWith {
            OperatorStatistics(Fraction.new(0), 1, Fraction.new(Int.MAX_VALUE.toLong()))
        }.toMutableMap()

}