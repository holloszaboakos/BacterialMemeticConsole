package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnFirstThatImproved<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val boostOperator: BoostOperator<S, C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<S, C>() {
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