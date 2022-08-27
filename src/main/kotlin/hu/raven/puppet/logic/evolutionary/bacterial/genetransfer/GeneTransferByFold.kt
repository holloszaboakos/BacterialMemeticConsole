package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneTransferByFold : GeneTransfer {
    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            repeat(injectionCount) { injectionIndex ->
                launch {
                    val specimenIndex = injectionIndex % (algorithm.sizeOfPopulation / 2)

                    val donor =
                        population[specimenIndex]
                    val acceptor =
                        population[algorithm.population.lastIndex - specimenIndex]

                    synchronized(acceptor) {
                        donor transferGeneTo acceptor
                    }
                }
            }
        }
    }
}