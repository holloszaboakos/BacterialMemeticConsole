package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.FloatArrayExtensions.vectorLength


class OrderPopulationByCost(
    val calculateCostOf: CalculateCost
) : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState> {

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.population.run {
        activesAsSequence()
            .filter { it.cost == null }
            .forEach { specimen ->
                specimen.cost = calculateCostOf(specimen)
            }

        sortActiveBy { it.costOrException().vectorLength() }
    }
}