package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

class BoostOnFirstThatImproved : Boost {
    val boostOperator: BoostOperator by KoinJavaComponent.inject(BoostOperator::class.java)
    var costPerPermutation = doubleArrayOf()

    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: SEvolutionaryAlgorithm<S>
    ) {
        if (costPerPermutation.isEmpty()) {
            costPerPermutation = DoubleArray(algorithm.sizeOfPopulation) { Double.MAX_VALUE }
        }

        algorithm.population
            .firstOrNull {
                it.cost < costPerPermutation[it.id]
            }
            ?.let {
                costPerPermutation[it.id] = it.cost
                boostOperator(algorithm, it)
            }
    }
}