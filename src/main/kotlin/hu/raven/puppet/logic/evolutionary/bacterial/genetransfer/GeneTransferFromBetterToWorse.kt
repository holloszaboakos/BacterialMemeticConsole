package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class GeneTransferFromBetterToWorse<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>
) : GeneTransfer<S> {
    val geneTransferOperator: GeneTransferOperator<S> by KoinJavaComponent.inject(GeneTransferOperator::class.java)
    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            val worse = population.slice(population.size / 2 until population.size)

            for (index in 0 until population.size / 2) {
                launch {

                    geneTransferOperator(population[index], worse[index])
                }
            }
        }
    }
}