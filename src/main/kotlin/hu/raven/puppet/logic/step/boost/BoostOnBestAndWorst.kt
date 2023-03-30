package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class BoostOnBestAndWorst<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val boostOperator: BoostOperator<S, C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<S, C>() {

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