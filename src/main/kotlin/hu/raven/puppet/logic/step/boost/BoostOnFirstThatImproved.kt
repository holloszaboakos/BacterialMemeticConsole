package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnFirstThatImproved<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    BoostFactory<C>() {
    var costPerPermutation = mutableListOf<C?>()

    override fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
            if (costPerPermutation.isEmpty()) {
                costPerPermutation = MutableList(population.size) { null }
            }

            population
                .firstOrNull {
                    costPerPermutation[it.id] != null && it.costOrException() < costPerPermutation[it.id]!!
                }
                ?.let {
                    costPerPermutation[it.id] = it.cost
                    val improvement = boostOperator(it)
                    synchronized(statistics) {
                        if (it == population.first()) {
                            statistics.boostOnBestImprovement = improvement
                        } else {
                            statistics.boostOnBestImprovement = StepEfficiencyData()
                        }
                        statistics.boostImprovement = improvement
                    }
                }
        }
}