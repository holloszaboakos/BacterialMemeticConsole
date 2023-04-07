package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit

sealed class GeneTransfer<C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<C> {
    abstract val injectionCount: Int
    abstract val geneTransferOperator: GeneTransferOperator<C>
}
