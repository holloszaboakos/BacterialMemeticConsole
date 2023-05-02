package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

sealed class BoostStrategy : EvolutionaryAlgorithmStep {
    protected abstract val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
}