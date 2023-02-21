package hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GeneTransferByTournament<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : GeneTransfer<S, C>() {
    val geneTransferOperator: GeneTransferOperator<S, C> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            if (population.size <= 1 || injectionCount == 0) {
                return@withContext
            }

            val populationRandomizer = (1 until population.size)
                .shuffled()

            val populationInRandomPairs = List(population.size - 1) {
                population[populationRandomizer[it]]
            }
                .chunked(2)
                .toMutableList()

            if (populationInRandomPairs.last().size == 1) {
                populationInRandomPairs.removeLast()
            }

            (0 until injectionCount)
                .map { injectionCount ->
                    async {
                        val specimen = populationInRandomPairs[injectionCount % populationInRandomPairs.size]
                            .sortedBy { it.cost!!.value.toDouble() }

                        synchronized(populationInRandomPairs[injectionCount % populationInRandomPairs.size][0]) {
                            synchronized(populationInRandomPairs[injectionCount % populationInRandomPairs.size][1]) {
                                geneTransferOperator(specimen[0], specimen[1])
                            }
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