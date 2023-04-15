package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class BoostOnBestLazy<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>
) :
    Boost<C>() {
    var costOfBest: C? = null

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val best = population.activesAsSequence().first()
        if (best.cost == costOfBest)
            boostOperator(best)
        costOfBest = best.cost
    }

}