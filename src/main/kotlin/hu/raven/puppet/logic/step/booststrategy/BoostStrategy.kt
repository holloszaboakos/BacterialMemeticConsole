package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.operator.boostoperator.BoostOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class BoostStrategy : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState> {
    protected abstract val boostOperator: BoostOperator<OnePartRepresentationWithCostAndIterationAndId>
}