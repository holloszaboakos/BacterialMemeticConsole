package hu.raven.puppet.logic.step.crossover_strategy

import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


abstract class CrossOverStrategy : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState<*>> {
    protected abstract val crossoverOperators: List<CrossOverOperator>
}