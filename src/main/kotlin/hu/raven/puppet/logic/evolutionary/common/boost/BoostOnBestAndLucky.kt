package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class BoostOnBestAndLucky(val luckyCount: Int) : Boost {
    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    val boostOperator: BoostOperator by inject(BoostOperator::class.java)

    override suspend fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>) {

        boostOperator(algorithm, algorithm.population.first())

        algorithm.population
            .slice(1 until algorithm.population.size)
            .shuffled()
            .slice(0 until luckyCount)
            .forEach {
                boostOperator(algorithm, it)
            }

    }
}