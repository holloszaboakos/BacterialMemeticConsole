package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class BoostOnBestAndWorst<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    val boostOperator: BoostOperator<S> by inject(BoostOperator::class.java)

    override suspend operator fun invoke(
    ): Unit = withContext(Dispatchers.Default) {
        launch {
            val best = algorithm.population.first()
            boostOperator(best)
        }
        launch {
            val worst = algorithm.population.last()
            boostOperator(worst)
        }
    }

}