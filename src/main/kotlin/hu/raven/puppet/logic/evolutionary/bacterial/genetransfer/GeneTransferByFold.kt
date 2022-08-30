package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class GeneTransferByFold<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>
) : GeneTransfer<S> {
    val geneTransferOperator: GeneTransferOperator<S> by KoinJavaComponent.inject(GeneTransferOperator::class.java)
    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            repeat(injectionCount) { injectionIndex ->
                launch {
                    val specimenIndex = injectionIndex % (algorithm.sizeOfPopulation / 2)

                    val donor =
                        population[specimenIndex]
                    val acceptor =
                        population[algorithm.population.lastIndex - specimenIndex]

                    synchronized(acceptor) {
                        geneTransferOperator(donor, acceptor)
                    }
                }
            }
        }
    }
}