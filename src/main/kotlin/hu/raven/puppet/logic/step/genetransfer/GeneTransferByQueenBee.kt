package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GeneTransferByQueenBee<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : hu.raven.puppet.logic.step.genetransfer.GeneTransfer<S, C>() {
    val geneTransferOperator: GeneTransferOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            val populationRandomizer = (1 until population.size)
                .shuffled()
                .toIntArray()
            (0 until injectionCount)
                .map { injectionIndex ->
                    async {
                        val acceptorIndex = populationRandomizer[injectionIndex % populationRandomizer.size]
                        val acceptor = population[acceptorIndex]

                        synchronized(acceptor) {
                            geneTransferOperator(population.first(), acceptor)
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