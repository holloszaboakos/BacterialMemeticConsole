package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice


class BoostOnBestAndLucky<C : PhysicsUnit<C>>(
    val luckyCount: Int,
    override val boostOperator: BoostOperator<C>
) : Boost<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {

        boostOperator(population.activesAsSequence().first())

        population.activesAsSequence()
            .slice(1 until population.activeCount)
            .shuffled()
            .slice(0 until luckyCount)
            .forEach { boostOperator(it) }
    }
}