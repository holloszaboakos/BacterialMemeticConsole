package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject


class BoostOnFirstThatImproved<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : hu.raven.puppet.logic.step.boost.Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()
    var costPerPermutation = mutableListOf<C?>()

    override suspend fun invoke() {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(sizeOfPopulation) { null }
        }

        algorithmState.population
            .firstOrNull {
                costPerPermutation[it.id] != null && it.costOrException() < costPerPermutation[it.id]!!
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