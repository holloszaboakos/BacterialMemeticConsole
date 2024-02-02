package hu.raven.puppet.logic.step.gene_transfer

import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferByQueenBee(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator,
) : GeneTransfer() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val populationRandomizer = (1..<population.activeCount)
            .shuffled()
            .toIntArray()
        (0..<injectionCount)
            .map { injectionIndex ->
                val acceptorIndex = populationRandomizer[injectionIndex % populationRandomizer.size]
                val acceptor = population[acceptorIndex]

                synchronized(acceptor) {
                    geneTransferOperator(population.activesAsSequence().first(), acceptor)
                }
            }
    }
}