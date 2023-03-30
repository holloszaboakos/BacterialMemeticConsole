package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.logging.DoubleLogger
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


class BoostOntTop<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    val boostedCount: Int,
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val boostOperator: BoostOperator<S, C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<S, C>() {

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