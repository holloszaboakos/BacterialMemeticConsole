package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class GeneTransfer<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    abstract val injectionCount: Int
    abstract val geneTransferOperator: GeneTransferOperator<S, C>
    abstract val statistics: BacterialAlgorithmStatistics

    abstract suspend operator fun invoke()
}
