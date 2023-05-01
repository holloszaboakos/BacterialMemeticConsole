package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator


abstract class CrossOverStrategy : EvolutionaryAlgorithmStep {
    protected abstract val crossoverOperators: List<CrossOverOperator>
}