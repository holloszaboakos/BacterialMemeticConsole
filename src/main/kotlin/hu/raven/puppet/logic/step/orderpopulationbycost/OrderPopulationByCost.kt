package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.inject


class OrderPopulationByCost<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    val calculateCostOf: CalculateCost<C> by inject()

    override operator fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        population
            .mapActives { it }
            .filter { it.content.cost == null }
            .forEach { specimen ->
                calculateCostOf(specimen.content)
            }

        population.sortActiveBy { it.content.costOrException().value }
    }
}