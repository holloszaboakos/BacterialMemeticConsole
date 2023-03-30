package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
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


class GeneTransferByElitist<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<S, C>,
    override val statistics: BacterialAlgorithmStatistics,
) :
    GeneTransfer<S, C>() {

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            val donorIndexRandomizer = (0 until population.size / 4)
                .shuffled()
                .toIntArray()
            val acceptorIndexRandomizer = (population.size / 4 until population.size)
                .shuffled()
                .toIntArray()

            (0 until injectionCount)
                .map { injectionIndex ->
                    async {
                        val donorIndex = donorIndexRandomizer[
                            injectionIndex % donorIndexRandomizer.size
                        ]
                        val donor = population[donorIndex]

                        val acceptorIndex = acceptorIndexRandomizer[
                            injectionIndex % acceptorIndexRandomizer.size
                        ]
                        val acceptor = population[acceptorIndex]

                        synchronized(acceptor) {
                            geneTransferOperator(donor, acceptor)
                        }
                    }
                }
                .map { it.await() }
                .sum()
                .also {
                    synchronized(statistics) {
                        statistics.geneTransferImprovement = it
                    }
                }
        }
    }
}