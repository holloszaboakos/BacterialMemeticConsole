package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnBest<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : BoostFactory<C>() {

    override operator fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
            val best = population.first()

            val improvement = boostOperator(best)

            synchronized(statistics) {
                statistics.boostImprovement = improvement
                statistics.boostOnBestImprovement = improvement
            }
        }

}