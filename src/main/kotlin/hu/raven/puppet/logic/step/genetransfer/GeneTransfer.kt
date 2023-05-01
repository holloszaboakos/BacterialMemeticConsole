package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator

sealed class GeneTransfer : EvolutionaryAlgorithmStep {
    abstract val injectionCount: Int
    abstract val geneTransferOperator: GeneTransferOperator
}
