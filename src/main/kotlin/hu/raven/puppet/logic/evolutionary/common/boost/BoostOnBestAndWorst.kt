package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class BoostOnBestAndWorst : Boost {
    val boostOperator: BoostOperator by KoinJavaComponent.inject(BoostOperator::class.java)

    override suspend operator fun <S : ISpecimenRepresentation> invoke(
        algorithm: SEvolutionaryAlgorithm<S>
    ): Unit = withContext(Dispatchers.Default) {
        launch {
            val best = algorithm.population.first()
            boostOperator(algorithm, best)
        }
        launch {
            val worst = algorithm.population.last()
            boostOperator(algorithm, worst)
        }
    }

}