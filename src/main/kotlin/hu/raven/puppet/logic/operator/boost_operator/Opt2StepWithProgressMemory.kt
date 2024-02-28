package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


class Opt2StepWithProgressMemory<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost<*>
) : BoostOperator<O>() {

    private var lastPosition = Pair(0, 1)

    override fun invoke(specimen: O) {
        outer@ for (firstIndex in lastPosition.first..<specimen.permutation.size - 1) {
            for (secondIndex in lastPosition.second..<specimen.permutation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    lastPosition = Pair(firstIndex, secondIndex)
                    return@invoke
                }
            }
        }

        lastPosition = Pair(0, 1)
    }
}