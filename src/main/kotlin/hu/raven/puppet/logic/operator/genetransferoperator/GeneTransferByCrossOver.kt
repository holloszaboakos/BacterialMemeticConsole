package hu.raven.puppet.logic.operator.genetransferoperator


import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


class GeneTransferByCrossOver(
    override val calculateCostOf: CalculateCost,
    override val geneTransferSegmentLength: Int,
    private val crossOverOperator: CrossOverOperator,
) : GeneTransferOperator() {

    override fun invoke(
        source: OnePartRepresentationWithCost,
        target: OnePartRepresentationWithCost
    ) {
        val child = target.cloneRepresentationAndCost()

        crossOverOperator(
            Pair(
                source.permutation,
                target.permutation
            ),
            child.permutation
        )

        child.cost = calculateCostOf(child)

        if (target.costOrException() dominatesSmaller child.costOrException()) {
            child.permutation.forEachIndexed { index, value ->
                target.permutation[index] = value
            }
            target.cost = child.cost
        }
    }
}