package hu.raven.puppet.logic.step.boost_strategy

import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnWorst(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIteration>,
) : BoostStrategy() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        val worst = population.activesAsSequence().last()
        boostOperator(worst.value)
    }
}