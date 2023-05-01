package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferByQueenBee(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator,
) : GeneTransfer() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val populationRandomizer = (1 until population.activeCount)
            .shuffled()
            .toIntArray()
        (0 until injectionCount)
            .map { injectionIndex ->
                val acceptorIndex = populationRandomizer[injectionIndex % populationRandomizer.size]
                val acceptor = population[acceptorIndex]

                synchronized(acceptor) {
                    geneTransferOperator(population.activesAsSequence().first(), acceptor)
                }
            }
    }
}