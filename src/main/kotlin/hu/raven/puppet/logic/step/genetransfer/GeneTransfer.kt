package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.operator.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep

sealed class GeneTransfer : EvolutionaryAlgorithmStep {
    abstract val injectionCount: Int
    abstract val geneTransferOperator: GeneTransferOperator
}
