package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnBestLazy<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<C>() {
    var costOfBest: C? = null

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val best = population.mapActives { it }.first()
        if (best.content.cost == costOfBest) {
            val improvement = boostOperator(best)
            synchronized(statistics) {
                statistics.boostImprovement = improvement
                statistics.boostOnBestImprovement = improvement
            }
        } else {
            val improvement = StepEfficiencyData()

            synchronized(statistics) {
                statistics.boostImprovement = improvement
                statistics.boostOnBestImprovement = improvement
            }
        }
        costOfBest = best.content.cost
    }

}