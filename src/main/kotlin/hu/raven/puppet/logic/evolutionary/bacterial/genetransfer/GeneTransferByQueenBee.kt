package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class GeneTransferByQueenBee<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>
) : GeneTransfer<S> {
    val geneTransferOperator: GeneTransferOperator<S> by KoinJavaComponent.inject(GeneTransferOperator::class.java)
    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            val populationRandomizer = (1 until population.size)
                .shuffled()
                .toIntArray()
            repeat(injectionCount) { injectionIndex ->
                launch {
                    val acceptorIndex = populationRandomizer[injectionIndex % populationRandomizer.size]
                    val acceptor = population[acceptorIndex]

                    synchronized(acceptor) {
                        geneTransferOperator(population.first(), acceptor)
                    }
                }
            }
        }
    }
}