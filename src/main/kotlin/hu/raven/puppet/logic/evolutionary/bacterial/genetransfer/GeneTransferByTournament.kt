package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneTransferByTournament : GeneTransfer {
    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            val populationRandomizer = (1 until population.size)
                .shuffled()

            val populationInRandomPairs = List(population.size - 1) {
                population[populationRandomizer[it]]
            }
                .chunked(2)
                .toMutableList()

            if(populationInRandomPairs.last().size == 1){
                populationInRandomPairs.removeLast()
            }

            repeat(injectionCount) { injectionCount ->
                launch {
                    val specimen = populationInRandomPairs[injectionCount % populationInRandomPairs.size]
                        .sortedBy { it.cost }

                    specimen[0] transferGeneTo specimen[1]
                }
            }
        }
    }
}