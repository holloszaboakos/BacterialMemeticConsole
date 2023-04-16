package hu.raven.puppet.logic.step.booststrategy

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnBest<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C, OnePartRepresentationWithCostAndIterationAndId<C>>
) : BoostStrategy<C>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val best = population.activesAsSequence().first()
        boostOperator(best)
    }

}