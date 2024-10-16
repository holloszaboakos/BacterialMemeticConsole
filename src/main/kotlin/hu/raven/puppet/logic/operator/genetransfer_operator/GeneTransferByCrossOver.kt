package hu.raven.puppet.logic.operator.genetransfer_operator


import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


class GeneTransferByCrossOver<T>(
    override val calculateCostOf: CalculateCost<T>,
    override val geneTransferSegmentLength: Int,
    private val crossOverOperator: CrossOverOperator,
) : GeneTransferOperator<T>() {

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
            target.permutation.clear()
            child.permutation.forEachIndexed { index, value ->
                target.permutation[index] = value
            }
            target.cost = child.cost
        }
    }
}