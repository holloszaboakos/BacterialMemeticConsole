package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject


class BoostOnSecond<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend operator fun invoke() {
        val secondBest = algorithmState.population[1]
        val improvement = boostOperator(secondBest)

        synchronized(statistics) {
            statistics.boostImprovement = improvement
        }
    }
}