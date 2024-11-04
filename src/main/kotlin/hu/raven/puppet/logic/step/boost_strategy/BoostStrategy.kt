package hu.raven.puppet.logic.step.boost_strategy

import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class BoostStrategy : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState<*>> {
    protected abstract val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIteration>
}