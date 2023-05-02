package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice


class BoostOnBestAndLucky(
    private val luckyCount: Int,
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        boostOperator(population.activesAsSequence().first())

        population.activesAsSequence()
            .slice(1 until population.activeCount)
            .shuffled()
            .slice(0 until luckyCount)
            .forEach { boostOperator(it) }
    }
}