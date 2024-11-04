package hu.raven.puppet.logic.step.boost_strategy

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnBestLazy(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIteration>
) : BoostStrategy() {
    private var costOfBest: FloatVector? = null

    override operator fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        val best = population.activesAsSequence().first()
        if (costOfBest?.let { best.value.costOrException() contentEquals it } != false)
            boostOperator(best.value)
        costOfBest = best.value.cost
    }

}