package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.utility.inject


class BoostOnBest<S : ISpecimenRepresentation> : Boost<S>() {
    val boostOperator: BoostOperator<S> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend operator fun invoke() {
        val best = algorithmState.population.first()

        val improvement = boostOperator(best)

        synchronized(statistics) {
            statistics.boostImprovement = improvement
            statistics.boostOnBestImprovement = improvement
        }
    }

}