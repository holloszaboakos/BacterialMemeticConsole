package hu.raven.puppet.logic.step.boost_strategy

import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnSecond<R>(
    override val boostOperator: BoostOperator<R, SolutionWithIteration<R>>
) : BoostStrategy<R>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        val secondBest = population[1]
        boostOperator(secondBest.value)
    }
}