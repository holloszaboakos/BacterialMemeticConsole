package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class BoostOnBestLazy<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    val boostOperator: BoostOperator<S> by inject(BoostOperator::class.java)
    var costOfBest = Double.MAX_VALUE

    override suspend operator fun invoke() {
        val best = algorithm.population.first()
        if (best.cost == costOfBest) {
            boostOperator(best)
        }
        costOfBest = best.cost
    }

}