package hu.raven.puppet.logic.step.gene_transfer

import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferByFold<R>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<R, SolutionWithIteration<R>>
) : GeneTransfer<R>() {

    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        (0..<injectionCount)
            .forEach { injectionIndex ->
                val specimenIndex = injectionIndex % (population.activeCount / 2)

                val donor =
                    population[specimenIndex]
                val acceptor =
                    population[population.activeCount - 1 - specimenIndex]

                synchronized(acceptor) {
                    geneTransferOperator(donor.value, acceptor.value)
                }
            }
    }
}