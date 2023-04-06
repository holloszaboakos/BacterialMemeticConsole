package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum


class BoostOnBestAndLucky<C : PhysicsUnit<C>>(
    val luckyCount: Int,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<C>() {

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