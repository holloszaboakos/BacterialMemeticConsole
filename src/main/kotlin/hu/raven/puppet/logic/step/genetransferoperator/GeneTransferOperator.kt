package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class GeneTransferOperator {
    abstract val calculateCostOf: CalculateCost
    abstract val geneTransferSegmentLength: Int

    abstract operator fun invoke(
        source: OnePartRepresentationWithCost,
        target: OnePartRepresentationWithCost
    )
}