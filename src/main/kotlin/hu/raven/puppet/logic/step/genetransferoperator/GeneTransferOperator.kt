package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject


sealed class GeneTransferOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {
    val calculateCostOf: CalculateCost<S, C> by inject()
    val geneTransferSegmentLength: Int by inject(AlgorithmParameters.GENE_TRANSFER_SEGMENT_LENGTH)

    abstract operator fun invoke(
        source: S,
        target: S
    ): StepEfficiencyData
}