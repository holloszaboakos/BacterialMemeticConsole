package hu.raven.puppet.logic.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneTransferFromBetterToWorse : GeneTransfer {
    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            val worse = population.slice(population.size / 2 until population.size)

            for (index in 0 until population.size / 2) {
                launch {
                    population[index] transferGeneTo worse[index]
                }
            }
        }
    }
}