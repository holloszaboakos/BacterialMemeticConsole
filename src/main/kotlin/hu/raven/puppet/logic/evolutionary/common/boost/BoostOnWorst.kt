package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

class BoostOnWorst : Boost {
    val boostOperator : BoostOperator by KoinJavaComponent.inject(BoostOperator::class.java)

    override suspend operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SEvolutionaryAlgorithm<S>
    ) {
        val worst = algorithm.population.last()
        boostOperator(algorithm, worst)
    }
}