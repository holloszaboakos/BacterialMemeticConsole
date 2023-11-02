package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.subordinatedBy


class BoostOnFirstThatImproved(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    private var costPerPermutation = mutableListOf<FloatArray?>()

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(population.activeCount) { null }
        }

        population.activesAsSequence()
            .firstOrNull {specimen->
                costPerPermutation[specimen.id]?.let{it subordinatedBy specimen.costOrException()} == true
            }
            ?.let {
                costPerPermutation[it.id] = it.cost
                boostOperator(it)
            }
    }
}