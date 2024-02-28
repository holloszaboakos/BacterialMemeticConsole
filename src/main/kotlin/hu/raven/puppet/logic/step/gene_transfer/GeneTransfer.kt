package hu.raven.puppet.logic.step.gene_transfer

import hu.raven.puppet.logic.operator.genetransfer_operator.GeneTransferOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class GeneTransfer : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState<*>> {
    protected abstract val injectionCount: Int
    protected abstract val geneTransferOperator: GeneTransferOperator<*>
}
