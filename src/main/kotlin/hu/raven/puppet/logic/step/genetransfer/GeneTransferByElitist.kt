package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.operator.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class GeneTransferByElitist(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator,
) : GeneTransfer() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
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
                    geneTransferOperator(donor, acceptor)
                }
            }
    }
}