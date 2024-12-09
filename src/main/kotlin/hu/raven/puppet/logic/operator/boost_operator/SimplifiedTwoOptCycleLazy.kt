package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


class SimplifiedTwoOptCycleLazy<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>
) : BoostOperator<Permutation, S>() {

    private var bestCost: FloatVector? = null
    private var improved = true

    override fun invoke(specimen: S) {
        if (!improved && bestCost?.let { it contentEquals specimen.costOrException() } == true) {
            return
        }

        improved = false

        for (firstIndex in 0..<specimen.representation.size - 1) {
            for (secondIndex in firstIndex + 1..<specimen.representation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    improved = true
                    bestCost = specimen.cost
                }
            }
        }
    }
}