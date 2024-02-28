package hu.raven.puppet.logic.step.boost_strategy

import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class NoBoost(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    override operator fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {}
}