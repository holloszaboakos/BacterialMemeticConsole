package hu.raven.puppet.logic.operator.genetransfer_operator


import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.model.solution.AlgorithmSolution


class GeneTransferByCrossOver<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>,
    override val geneTransferSegmentLength: Int,
    private val crossOverOperator: CrossOverOperator<Permutation>,
) : GeneTransferOperator<Permutation, S>() {

    override fun invoke(
        source: S,
        target: S
    ) {
        val child = target.clone()

        crossOverOperator(
            Pair(
                source.representation,
                target.representation
            ),
            child.representation
        )

        child.cost = calculateCostOf(child.representation)

        if (target.costOrException() dominatesSmaller child.costOrException()) {
            target.representation.clear()
            child.representation.forEachIndexed { index, value ->
                target.representation[index] = value
            }
            target.cost = child.cost
        }
    }
}