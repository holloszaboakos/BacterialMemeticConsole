package hu.raven.puppet.logic.step.order_population_by_cost

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class OrderPopulationByCost<T>(
    val calculateCostOf: CalculateCost<T>
) : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState<T>> {

    override operator fun invoke(state: EvolutionaryAlgorithmState<T>): Unit = state.population.run {
        if (activesAsSequence().first().value.permutation.size <= 1)
            return@run

        activesAsSequence()
            .filter { it.value.cost == null }
            .forEach { specimen ->
                specimen.value.cost = calculateCostOf(specimen.value)
            }

        sortActiveBy { it.value.costOrException().length().toFloat() }
    }
}