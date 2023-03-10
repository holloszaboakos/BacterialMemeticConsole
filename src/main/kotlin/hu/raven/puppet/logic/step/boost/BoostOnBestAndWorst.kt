package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class BoostOnBestAndWorst<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : hu.raven.puppet.logic.step.boost.Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

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