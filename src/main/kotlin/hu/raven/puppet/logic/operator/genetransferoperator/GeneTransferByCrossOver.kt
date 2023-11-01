package hu.raven.puppet.logic.operator.genetransferoperator


import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.FloatArrayExtensions.dominatedBy

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

        if (child.costOrException() dominatedBy target.costOrException()) {
            child.permutation.forEachIndexed { index, value ->
                target.permutation[index] = value
            }
            target.cost = child.cost
        }
    }
}