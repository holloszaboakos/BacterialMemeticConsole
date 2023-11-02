package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.matches


class BoostOnBestLazy(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    private var costOfBest: FloatArray? = null

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val best = population.activesAsSequence().first()
        if (costOfBest?.let { best.costOrException() matches  it} != false)
            boostOperator(best)
        costOfBest = best.cost
    }

}