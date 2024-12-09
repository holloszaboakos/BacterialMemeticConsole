package hu.raven.puppet.logic.step.gene_transfer

import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class GeneTransferByElitist<R>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<R, SolutionWithIteration<R>>,
) : GeneTransfer<R>() {

    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        val donorIndexRandomizer = (0..<population.activeCount / 4)
            .shuffled()
            .toIntArray()
        val acceptorIndexRandomizer = (population.activeCount / 4..<population.activeCount)
            .shuffled()
            .toIntArray()

        (0..<injectionCount)
            .forEach { injectionIndex ->
                val donorIndex = donorIndexRandomizer[
                    injectionIndex % donorIndexRandomizer.size
                ]
                val donor = population[donorIndex]

                val acceptorIndex = acceptorIndexRandomizer[
                    injectionIndex % acceptorIndexRandomizer.size
                ]
                val acceptor = population[acceptorIndex]

                synchronized(acceptor) {
                    geneTransferOperator(donor.value, acceptor.value)
                }
            }
    }
}