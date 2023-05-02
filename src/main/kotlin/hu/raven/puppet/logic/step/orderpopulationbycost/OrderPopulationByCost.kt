package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class OrderPopulationByCost(
    val calculateCostOf: CalculateCost
) : EvolutionaryAlgorithmStep {

    override operator fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        population
            .activesAsSequence()
            .filter { it.cost == null }
            .forEach { specimen ->
                specimen.cost = calculateCostOf(specimen)
            }

        population.sortActiveBy { it.costOrException() }
    }
}