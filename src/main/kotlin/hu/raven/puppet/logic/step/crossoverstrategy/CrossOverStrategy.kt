package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


abstract class CrossOverStrategy : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState> {
    protected abstract val crossoverOperators: List<CrossOverOperator>
}