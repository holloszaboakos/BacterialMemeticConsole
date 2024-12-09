package hu.raven.puppet.logic.step.boost_strategy

import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class BoostStrategy<R> : EvolutionaryAlgorithmStep<R, EvolutionaryAlgorithmState<R>> {
    protected abstract val boostOperator: BoostOperator<R, SolutionWithIteration<R>>
}