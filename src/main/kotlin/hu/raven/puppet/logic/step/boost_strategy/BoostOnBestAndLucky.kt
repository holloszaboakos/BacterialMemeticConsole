package hu.raven.puppet.logic.step.boost_strategy

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnBestAndLucky(
    private val luckyCount: Int,
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIteration>
) : BoostStrategy() {

    override fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        boostOperator(population.activesAsSequence().first().value)

        population.activesAsSequence()
            .slice(1..<population.activeCount)
            .shuffled()
            .slice(0..<luckyCount)
            .forEach { boostOperator(it.value) }
    }
}