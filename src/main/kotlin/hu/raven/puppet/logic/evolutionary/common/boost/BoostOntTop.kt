package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class BoostOntTop<S : ISpecimenRepresentation>(
    val boostedCount: Int,
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    val boostOperator: BoostOperator<S> by inject(BoostOperator::class.java)

    override suspend operator fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithm.population
            .slice(0 until boostedCount)
            .forEach {
                launch {
                    boostOperator(it)
                }
            }
    }

}