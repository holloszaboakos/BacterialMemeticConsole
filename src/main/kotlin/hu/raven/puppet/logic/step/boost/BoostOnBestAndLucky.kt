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
) : BoostFactory<C>() {

    override fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {

            val boostOnBestImprovement = boostOperator(population.first())

            synchronized(statistics) {
                statistics.boostOnBestImprovement = boostOnBestImprovement
            }

            population
                .slice(1 until population.size)
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