package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class BoostOnBestAndLucky<S : ISpecimenRepresentation>(
    val luckyCount: Int,
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    val boostOperator: BoostOperator<S> by inject(BoostOperator::class.java)

    override suspend fun invoke() {

        boostOperator(algorithm.population.first())

        algorithm.population
            .slice(1 until algorithm.population.size)
            .shuffled()
            .slice(0 until luckyCount)
            .forEach {
                boostOperator(it)
            }

    }
}