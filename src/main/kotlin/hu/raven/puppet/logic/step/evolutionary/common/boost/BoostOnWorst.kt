package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject


class BoostOnWorst<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend operator fun invoke() {
        val worst = algorithmState.population.last()
        val improvement = boostOperator(worst)

        synchronized(statistics) {
            statistics.boostImprovement = improvement
        }
    }
}