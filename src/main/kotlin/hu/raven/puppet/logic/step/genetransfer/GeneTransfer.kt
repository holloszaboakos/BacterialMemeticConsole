package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class GeneTransfer<C : PhysicsUnit<C>> {
    abstract val injectionCount: Int
    abstract val geneTransferOperator: GeneTransferOperator<C>
    abstract val statistics: BacterialAlgorithmStatistics

    abstract suspend operator fun invoke()
}
