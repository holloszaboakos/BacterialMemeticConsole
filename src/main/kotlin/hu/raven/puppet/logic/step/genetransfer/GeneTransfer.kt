package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.operator.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

sealed class GeneTransfer : EvolutionaryAlgorithmStep<EvolutionaryAlgorithmState> {
    protected abstract val injectionCount: Int
    protected abstract val geneTransferOperator: GeneTransferOperator
}
