package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost

class Opt2Step<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost<*>
) : BoostOperator<O>() {

    override fun invoke(specimen: O) {
        for (firstIndex in 0..<specimen.permutation.size - 1) {
            for (secondIndex in firstIndex + 1..<specimen.permutation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    return@invoke
                }
            }
        }
    }
}