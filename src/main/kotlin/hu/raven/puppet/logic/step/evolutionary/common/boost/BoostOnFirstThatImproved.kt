package hu.raven.puppet.logic.step.evolutionary.common.boost

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.utility.inject


class BoostOnFirstThatImproved<S : ISpecimenRepresentation> : Boost<S>() {
    val boostOperator: BoostOperator<S> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()
    var costPerPermutation = doubleArrayOf()

    override suspend fun invoke() {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = DoubleArray(sizeOfPopulation) { Double.MAX_VALUE }
        }

        algorithmState.population
            .firstOrNull {
                it.cost < costPerPermutation[it.id]
            }
            ?.let {
                costPerPermutation[it.id] = it.cost
                val improvement = boostOperator(it)
                synchronized(statistics) {
                    if (it == algorithmState.population.first()) {
                        statistics.boostOnBestImprovement = improvement
                    } else {
                        statistics.boostOnBestImprovement = StepEfficiencyData()
                    }
                    statistics.boostImprovement = improvement
                }
            }
    }
}