package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


class SimplifiedTwoOptCycle<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>
) : BoostOperator<Permutation, S>() {

    override fun invoke(specimen: S) {
        for (firstIndex in 0..<specimen.representation.size - 1) {
            for (secondIndex in firstIndex + 1..<specimen.representation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                )
            }
        }
    }
}