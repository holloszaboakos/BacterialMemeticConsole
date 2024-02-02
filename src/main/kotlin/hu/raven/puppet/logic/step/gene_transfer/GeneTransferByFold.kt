package hu.raven.puppet.logic.step.gene_transfer

import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferByFold(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator
) : GeneTransfer() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        (0..<injectionCount)
            .forEach { injectionIndex ->
                val specimenIndex = injectionIndex % (population.activeCount / 2)

                val donor =
                    population[specimenIndex]
                val acceptor =
                    population[population.activeCount - 1 - specimenIndex]

                synchronized(acceptor) {
                    geneTransferOperator(donor, acceptor)
                }
            }
    }
}