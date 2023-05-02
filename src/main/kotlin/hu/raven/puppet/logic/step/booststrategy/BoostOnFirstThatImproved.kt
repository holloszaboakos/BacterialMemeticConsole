package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnFirstThatImproved(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    var costPerPermutation = mutableListOf<Fraction?>()

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(population.activeCount) { null }
        }

        population.activesAsSequence()
            .firstOrNull {
                costPerPermutation[it.id] != null && it.costOrException() < costPerPermutation[it.id]!!
            }
            ?.let {
                costPerPermutation[it.id] = it.cost
                boostOperator(it)
            }
    }
}