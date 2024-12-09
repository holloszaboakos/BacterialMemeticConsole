package hu.raven.puppet.logic.operator.genetransfer_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


sealed class GeneTransferOperator<R, S : AlgorithmSolution<R, S>> {
    protected abstract val calculateCostOf: CalculateCost<R, *>
    protected abstract val geneTransferSegmentLength: Int

    abstract operator fun invoke(
        source: S,
        target: S
    )
}