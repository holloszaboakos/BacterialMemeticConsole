package hu.raven.puppet.logic.step.evolutionary.common

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class OrderPopulationByCost<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    val calculateCostOf: CalculateCost<S> by inject()

    suspend operator fun invoke(
    ) = withContext(Dispatchers.Default) {
        algorithmState.run {
            population.asFlow()
                .filter { !it.costCalculated }
                .map { specimen ->
                    calculateCostOf(specimen)
                }.collect()

            population.sortBy { it.cost }

            population.forEachIndexed { index, it ->
                it.orderInPopulation = index
                it.inUse = false
            }
        }
    }
}