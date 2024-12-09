package hu.raven.puppet.logic.step.boost_strategy

import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnWorst<R>(
    override val boostOperator: BoostOperator<R, SolutionWithIteration<R>>,
) : BoostStrategy<R>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        val worst = population.activesAsSequence().last()
        boostOperator(worst.value)
    }
}