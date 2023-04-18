package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class OrderPopulationByCost<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>
) : EvolutionaryAlgorithmStep<C> {

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        population
            .activesAsSequence()
            .filter { it.cost == null }
            .forEach { specimen ->
                specimen.cost = calculateCostOf(specimen)
            }

        population.sortActiveBy { it.costOrException().value }
    }
}