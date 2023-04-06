package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.EvolutionaryAlgorithmStepFactory
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.inject


class OrderPopulationByCostFactory<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStepFactory<C> {
    val calculateCostOf: CalculateCost<C> by inject()

    override operator fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
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