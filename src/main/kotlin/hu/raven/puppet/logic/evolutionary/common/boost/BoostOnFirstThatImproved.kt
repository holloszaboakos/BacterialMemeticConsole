package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class BoostOnFirstThatImproved<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    val boostOperator: BoostOperator<S> by inject(BoostOperator::class.java)
    var costPerPermutation = doubleArrayOf()

    override suspend fun invoke() {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = DoubleArray(algorithm.sizeOfPopulation) { Double.MAX_VALUE }
        }

        algorithm.population
            .firstOrNull {
                it.cost < costPerPermutation[it.id]
            }
            ?.let {
                costPerPermutation[it.id] = it.cost
                boostOperator(it)
            }
    }
}