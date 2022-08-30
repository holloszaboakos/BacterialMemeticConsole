package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class GeneTransferByTournament<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>
) : GeneTransfer<S> {
    val geneTransferOperator: GeneTransferOperator<S> by KoinJavaComponent.inject(GeneTransferOperator::class.java)
    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
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

            repeat(injectionCount) { injectionCount ->
                launch {
                    val specimen = populationInRandomPairs[injectionCount % populationInRandomPairs.size]
                        .sortedBy { it.cost }


                    geneTransferOperator(specimen[0], specimen[1])
                }
            }
        }
    }
}