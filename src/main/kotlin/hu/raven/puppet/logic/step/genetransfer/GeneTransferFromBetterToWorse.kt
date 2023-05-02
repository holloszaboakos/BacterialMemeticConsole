package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.operator.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class GeneTransferFromBetterToWorse(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator,
) : GeneTransfer() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val worse = population
            .activesAsSequence()
            .slice(population.activeCount / 2 until population.activeCount)
            .toList()

        (0 until population.activeCount / 2)
            .forEach { index ->
                geneTransferOperator(population[index], worse[index])
            }
    }
}