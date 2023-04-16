package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice


class BoostOntTop<C : PhysicsUnit<C>>(
    val boostedCount: Int,
    override val boostOperator: BoostOperator<C, OnePartRepresentationWithCostAndIterationAndId<C>>,
) : BoostStrategy<C>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        population.activesAsSequence()
            .slice(0 until boostedCount)
            .forEach {
                boostOperator(it)
            }
    }

}