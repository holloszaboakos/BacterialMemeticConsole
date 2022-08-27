package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneTransferByElitist : GeneTransfer {
    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ) = withContext(Dispatchers.Default) {
        algorithm.run {
            val donorIndexRandomizer = (0 until population.size / 4)
                .shuffled()
                .toIntArray()
            val acceptorIndexRandomizer = (population.size / 4 until population.size)
                .shuffled()
                .toIntArray()

            repeat(injectionCount) { injectionIndex ->
                launch {
                    val donorIndex = donorIndexRandomizer[
                            injectionIndex % donorIndexRandomizer.size
                    ]
                    val donor = population[donorIndex]

                    val acceptorIndex = acceptorIndexRandomizer[
                            injectionIndex % acceptorIndexRandomizer.size
                    ]
                    val acceptor = population[acceptorIndex]

                    synchronized(acceptor) {
                        donor transferGeneTo acceptor
                    }
                }
            }
        }
    }
}