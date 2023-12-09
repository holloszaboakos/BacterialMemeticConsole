package hu.raven.puppet.logic.step.booststrategy

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.logic.operator.boostoperator.BoostOperator

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnBestLazy(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    private var costOfBest: FloatVector? = null

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val best = population.activesAsSequence().first()
        if (costOfBest?.let { best.costOrException() contentEquals it } != false)
            boostOperator(best)
        costOfBest = best.cost
    }

}