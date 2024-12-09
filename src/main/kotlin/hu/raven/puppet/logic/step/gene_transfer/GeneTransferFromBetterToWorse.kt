package hu.raven.puppet.logic.step.gene_transfer

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferFromBetterToWorse<R>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<R, SolutionWithIteration<R>>,
) : GeneTransfer<R>() {

    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        val worse = population
            .activesAsSequence()
            .slice(population.activeCount / 2..<population.activeCount)
            .toList()

        (0..<population.activeCount / 2)
            .forEach { index ->
                geneTransferOperator(population[index].value, worse[index].value)
            }
    }
}