package hu.raven.puppet.logic.step.boost_strategy

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOntTop(
    private val boostedCount: Int,
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>,
) : BoostStrategy() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        population.activesAsSequence()
            .slice(0..<boostedCount)
            .forEach {
                boostOperator(it)
            }
    }

}