package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum


class BoostOnBestAndLucky<C : PhysicsUnit<C>>(
    val luckyCount: Int,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {

        val boostOnBestImprovement = boostOperator(population.mapActives { it }.first())

        synchronized(statistics) {
            statistics.boostOnBestImprovement = boostOnBestImprovement
        }

        population.mapActives { it }
            .slice(1 until population.mapActives { it }.size)
            .shuffled()
            .slice(0 until luckyCount)
            .map { boostOperator(it) }
            .let { it + boostOnBestImprovement }
            .sum()
            .also {
                synchronized(statistics) {
                    statistics.boostImprovement = it
                }
            }

    }
}