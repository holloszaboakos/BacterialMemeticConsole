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
            .filter { it.cost == null }
            .forEach { specimen ->
                calculateCostOf(specimen)
            }

        population.sortBy { it.costOrException().value }

        population.forEachIndexed { index, it ->
            it.orderInPopulation = index
            it.inUse = false
        }
    }
}