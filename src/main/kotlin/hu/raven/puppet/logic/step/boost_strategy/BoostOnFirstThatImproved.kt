package hu.raven.puppet.logic.step.boost_strategy

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnFirstThatImproved<R>(
    override val boostOperator: BoostOperator<R, SolutionWithIteration<R>>
) : BoostStrategy<R>() {
    private var costPerPermutation = mutableListOf<FloatVector?>()

    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(population.activeCount) { null }
        }

        population.activesAsSequence()
            .firstOrNull { specimen ->
                costPerPermutation[specimen.index]?.let { it dominatesSmaller specimen.value.costOrException() } == true
            }
            ?.let {
                costPerPermutation[it.index] = it.value.cost
                boostOperator(it.value)
            }
    }
}