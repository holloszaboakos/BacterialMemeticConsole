package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


class Opt2Optimization<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost
) : BoostOperator<O>() {

    override fun invoke(specimen: O) {
        var improved = true

        while (improved) {
            improved = false
            for (firstIndex in 0..<specimen.permutation.size - 1) {
                for (secondIndex in firstIndex + 1..<specimen.permutation.size) {
                    swapIfBetter(
                        specimen,
                        firstIndex,
                        secondIndex,
                        calculateCostOf
                    ) {
                        improved = true
                    }
                }
            }
        }
    }
}