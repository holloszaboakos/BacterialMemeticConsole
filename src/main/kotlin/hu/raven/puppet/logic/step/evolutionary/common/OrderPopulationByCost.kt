package hu.raven.puppet.logic.step.evolutionary.common

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class OrderPopulationByCost<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    val calculateCostOf: CalculateCost<S, C> by inject()

    suspend operator fun invoke(
    ) = withContext(Dispatchers.Default) {
        algorithmState.run {
            population.asFlow()
                .filter { !it.costCalculated }
                .map { specimen ->
                    calculateCostOf(specimen)
                }.collect()

            population.sortBy { it.cost!!.value.toDouble() }

            population.forEachIndexed { index, it ->
                it.orderInPopulation = index
                it.inUse = false
            }
        }
    }
}