package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep


abstract class CrossOverStrategy : EvolutionaryAlgorithmStep {
    protected abstract val crossoverOperators: List<CrossOverOperator>
}