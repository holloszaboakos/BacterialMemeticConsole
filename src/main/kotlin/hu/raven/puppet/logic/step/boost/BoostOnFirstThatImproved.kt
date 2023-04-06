package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnFirstThatImproved<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<C>() {
    var costPerPermutation = mutableListOf<C?>()

    override suspend fun invoke() {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = MutableList(parameters.sizeOfPopulation) { null }
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