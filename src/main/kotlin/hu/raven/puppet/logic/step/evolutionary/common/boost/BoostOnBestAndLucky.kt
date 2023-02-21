package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject


class BoostOnBestAndLucky<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>>(
    val luckyCount: Int
) : Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend fun invoke() {

        val boostOnBestImprovement = boostOperator(algorithmState.population.first())

        synchronized(statistics) {
            statistics.boostOnBestImprovement = boostOnBestImprovement
        }

        algorithmState.population
            .slice(1 until algorithmState.population.size)
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