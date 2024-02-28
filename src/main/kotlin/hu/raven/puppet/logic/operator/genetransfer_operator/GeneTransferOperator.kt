package hu.raven.puppet.logic.operator.genetransfer_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class GeneTransferOperator<T> {
    protected abstract val calculateCostOf: CalculateCost<T>
    protected abstract val geneTransferSegmentLength: Int

    abstract operator fun invoke(
        source: OnePartRepresentationWithCost,
        target: OnePartRepresentationWithCost
    )
}