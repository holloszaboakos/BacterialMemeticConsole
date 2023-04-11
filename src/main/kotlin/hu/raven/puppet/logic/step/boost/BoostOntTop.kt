package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum


class BoostOntTop<C : PhysicsUnit<C>>(
    val boostedCount: Int,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<C>() {

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        population.mapActives { it }
            .slice(0 until boostedCount)
            .map {
                boostOperator(it)
            }
            .map { it }
            .onEachIndexed { index, it ->
                if (index == 0) {
                    synchronized(statistics) {
                        statistics.boostOnBestImprovement = it
                    }
                }
            }
            .sum()
            .also {
                synchronized(statistics) {
                    statistics.boostImprovement = it
                }
            }
    }

}