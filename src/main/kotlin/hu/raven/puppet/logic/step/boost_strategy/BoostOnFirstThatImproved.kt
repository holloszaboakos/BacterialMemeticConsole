package hu.raven.puppet.logic.step.boost_strategy

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnFirstThatImproved(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    private var costPerPermutation = mutableListOf<FloatVector?>()

    override fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(population.activeCount) { null }
        }

        population.activesAsSequence()
            .firstOrNull { specimen ->
                costPerPermutation[specimen.id]?.let { it dominatesSmaller specimen.costOrException() } == true
            }
            ?.let {
                costPerPermutation[it.id] = it.cost
                boostOperator(it)
            }
    }
}