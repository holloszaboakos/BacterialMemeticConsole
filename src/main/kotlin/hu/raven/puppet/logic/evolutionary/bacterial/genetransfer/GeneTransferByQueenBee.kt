package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneTransferByQueenBee : GeneTransfer {
    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            val populationRandomizer = (1 until population.size)
                .shuffled()
                .toIntArray()
            repeat(injectionCount) { injectionIndex ->
                launch {
                    val acceptorIndex = populationRandomizer[injectionIndex % populationRandomizer.size]
                    val acceptor = population[acceptorIndex]

                    synchronized(acceptor) {
                        population.first() transferGeneTo acceptor
                    }
                }
            }
        }
    }
}