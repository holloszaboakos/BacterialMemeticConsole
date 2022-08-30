package hu.raven.puppet.logic.evolutionary.common

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class OrderPopulationByCost<S : ISpecimenRepresentation>(
    val algorithm: SEvolutionaryAlgorithm<S>
) {
    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)

    suspend operator fun invoke(
    ) = withContext(Dispatchers.Default) {
        algorithm.run {
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