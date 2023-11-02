package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.FloatArrayExtensions.matches


class Opt2CycleLazy<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost
) : BoostOperator<O>() {

    private var bestCost: FloatArray? = null
    private var improved = true

    override fun invoke(specimen: O) {
        if (!improved && bestCost?.let { it matches specimen.costOrException() } == true) {
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