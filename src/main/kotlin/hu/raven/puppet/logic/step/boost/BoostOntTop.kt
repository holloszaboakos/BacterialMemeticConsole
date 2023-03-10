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


class BoostOntTop<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    val boostedCount: Int
) : hu.raven.puppet.logic.step.boost.Boost<S, C>() {
    val boostOperator: BoostOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

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