package hu.raven.puppet.logic.step.genetransferoperator


import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost

class GeneTransferByCrossOver<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>,
    override val geneTransferSegmentLength: Int,
    val crossOverOperator: CrossOverOperator,
) : GeneTransferOperator<C>() {

    override fun invoke(
        source: OnePartRepresentationWithCost<C>,
        target: OnePartRepresentationWithCost<C>
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

        if (child.costOrException() < target.costOrException()) {
            child.permutation.forEachIndexed { index, value ->
                target.permutation[index] = value
            }
            target.cost = child.cost
        }
    }
}