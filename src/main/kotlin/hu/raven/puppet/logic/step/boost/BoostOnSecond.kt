package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnSecond<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : BoostFactory<C>() {

    override operator fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
            val secondBest = population[1]
            val improvement = boostOperator(secondBest)

            synchronized(statistics) {
                statistics.boostImprovement = improvement
            }
        }
}