package hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.common.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.evolutionary.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject


sealed class GeneTransferOperator<S : ISpecimenRepresentation> : EvolutionaryAlgorithmStep<S>() {
    val calculateCostOf: CalculateCost<S> by inject()
    val geneTransferSegmentLength: Int by inject(AlgorithmParameters.GENE_TRANSFER_SEGMENT_LENGTH)

    abstract operator fun invoke(
        source: S,
        target: S
    ): StepEfficiencyData
}