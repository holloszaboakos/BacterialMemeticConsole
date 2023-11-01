package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.operator.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep

sealed class GeneTransfer : EvolutionaryAlgorithmStep {
    protected abstract val injectionCount: Int
    protected abstract val geneTransferOperator: GeneTransferOperator
}
