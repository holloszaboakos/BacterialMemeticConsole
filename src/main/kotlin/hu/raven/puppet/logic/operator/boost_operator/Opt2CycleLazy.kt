package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


class Opt2CycleLazy<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost
) : BoostOperator<O>() {

    private var bestCost: FloatVector? = null
    private var improved = true

    override fun invoke(specimen: O) {
        if (!improved && bestCost?.let { it contentEquals specimen.costOrException() } == true) {
            return
        }

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
                    bestCost = specimen.cost
                }
            }
        }
    }
}