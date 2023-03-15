package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GeneTransferByFold<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    GeneTransfer<S, C>() {
    val geneTransferOperator: GeneTransferOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            (0 until injectionCount)
                .map { injectionIndex ->
                    async {
                        val specimenIndex = injectionIndex % (sizeOfPopulation / 2)

                        val donor =
                            population[specimenIndex]
                        val acceptor =
                            population[population.lastIndex - specimenIndex]

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