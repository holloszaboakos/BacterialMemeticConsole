package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.inject


class OrderPopulationByCost<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    val calculateCostOf: CalculateCost<C> by inject()

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