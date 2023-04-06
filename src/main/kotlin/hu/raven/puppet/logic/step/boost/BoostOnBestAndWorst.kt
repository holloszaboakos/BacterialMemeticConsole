package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class BoostOnBestAndWorst<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<C>() {

    override suspend operator fun invoke(
    ): Unit = withContext(Dispatchers.Default) {
        listOf(
            async {
                val best = algorithmState.population.first()
                boostOperator(best)
            },
            async {
                val worst = algorithmState.population.last()
                boostOperator(worst)
            }
        )
            .map { it.await() }
            .onEachIndexed { index, stepEfficiencyData ->
                if (index == 0) {
                    synchronized(statistics) {
                        statistics.boostOnBestImprovement = stepEfficiencyData
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