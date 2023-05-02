package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice


class BoostOntTop(
    val boostedCount: Int,
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>,
) : BoostStrategy() {

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        population.activesAsSequence()
            .slice(0 until boostedCount)
            .forEach {
                boostOperator(it)
            }
    }

}