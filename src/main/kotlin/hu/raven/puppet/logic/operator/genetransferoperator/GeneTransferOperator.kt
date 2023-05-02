package hu.raven.puppet.logic.operator.genetransferoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class GeneTransferOperator {
    abstract val calculateCostOf: CalculateCost
    abstract val geneTransferSegmentLength: Int

    abstract operator fun invoke(
        source: OnePartRepresentationWithCost,
        target: OnePartRepresentationWithCost
    )
}