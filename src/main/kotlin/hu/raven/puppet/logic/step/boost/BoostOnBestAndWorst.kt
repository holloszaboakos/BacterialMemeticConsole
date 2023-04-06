package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.runBlocking


class BoostOnBestAndWorst<C : PhysicsUnit<C>>(
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    BoostFactory<C>() {

    override operator fun invoke() =
        fun EvolutionaryAlgorithmState<C>.(): Unit = runBlocking {
            listOf(
                {
                    val best = population.first()
                    boostOperator(best)
                },
                {
                    val worst = population.last()
                    boostOperator(worst)
                }
            )
                .map { it() }
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