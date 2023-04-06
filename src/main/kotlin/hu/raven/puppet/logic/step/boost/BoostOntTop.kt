package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class BoostOntTop<C : PhysicsUnit<C>>(
    val boostedCount: Int,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<C>() {

    override suspend operator fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.population
            .slice(0 until boostedCount)
            .map {
                async {
                    boostOperator(it)
                }
            }
            .map { it.await() }
            .onEachIndexed { index, it ->
                if (index == 0) {
                    synchronized(statistics) {
                        statistics.boostOnBestImprovement = it
                    }
                }
            }
            .sum()
            .also {
                synchronized(statistics) {
                    statistics.boostImprovement = it
                }
            }
    }

}