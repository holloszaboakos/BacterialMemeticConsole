package hu.raven.puppet.logic.step.order_population_by_cost

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.AlgorithmTask


class OrderPopulationByCost<R, T : AlgorithmTask>(
    val calculateCostOf: CalculateCost<R, T>
) : EvolutionaryAlgorithmStep<R, EvolutionaryAlgorithmState<R>> {

    override operator fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.population.run {
        activesAsSequence()
            .filter { it.value.cost == null }
            .forEach { specimen ->
                specimen.value.cost = calculateCostOf(specimen.value.representation)
            }

        sortActiveBy { it.value.costOrException().length().toFloat() }
    }
}