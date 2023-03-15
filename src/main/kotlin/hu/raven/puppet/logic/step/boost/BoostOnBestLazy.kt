package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.inject


class BoostOnBestLazy<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()
    var costOfBest: C? = null

    override suspend operator fun invoke() {
        val best = algorithmState.population.first()
        if (best.cost == costOfBest) {
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
        costOfBest = best.cost
    }

}