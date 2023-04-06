package hu.raven.puppet.logic.step.orderpopulationbycost

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class OrderPopulationByCost<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
){
    val calculateCostOf: CalculateCost<C> by inject()

    suspend operator fun invoke(
    ) = withContext(Dispatchers.Default) {
        algorithmState.run {
            population.asFlow()
                .filter { it.cost == null }
                .map { specimen ->
                    calculateCostOf(specimen)
                }.collect()

            population.sortBy { it.costOrException().value }

            population.forEachIndexed { index, it ->
                it.orderInPopulation = index
                it.inUse = false
            }
        }
    }
}