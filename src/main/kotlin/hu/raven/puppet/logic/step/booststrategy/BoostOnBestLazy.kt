package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnBestLazy(
    override val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
) : BoostStrategy() {
    private var costOfBest: Fraction? = null

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val best = population.activesAsSequence().first()
        if (best.cost == costOfBest)
            boostOperator(best)
        costOfBest = best.cost
    }

}