package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

class BoostOnBestLazy : Boost {
    val boostOperator: BoostOperator by KoinJavaComponent.inject(BoostOperator::class.java)
    var costOfBest = Double.MAX_VALUE

    override suspend operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SEvolutionaryAlgorithm<S>
    ) {
        val best = algorithm.population.first()
        if (best.cost == costOfBest) {
            boostOperator(algorithm, best)
        }
        costOfBest = best.cost
    }

}