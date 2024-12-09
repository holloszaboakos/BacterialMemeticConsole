package hu.raven.puppet.logic.step.boost_strategy

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.logic.operator.boost_operator.BoostOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOntTop<R>(
    private val boostedCount: Int,
    override val boostOperator: BoostOperator<R, SolutionWithIteration<R>>,
) : BoostStrategy<R>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        population.activesAsSequence()
            .slice(0..<boostedCount)
            .forEach {
                boostOperator(it.value)
            }
    }

}