package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost

import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.FloatArrayExtensions.matches
import hu.raven.puppet.utility.extention.FloatArrayExtensions.notDominatedBy


class Opt2CycleLazy<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost
) : BoostOperator<O>() {

    var bestCost: FloatArray? = null
    private var improved = true

    override fun invoke(specimen: O) {
        if (!improved && bestCost!! matches specimen.costOrException()) {
            return
        }

        improved = false
        bestCost = specimen.cost

        for (firstIndex in 0 ..<specimen.permutation.size - 1) {
            for (secondIndex in firstIndex + 1 ..<specimen.permutation.size) {
                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

                if (specimen.costOrException() notDominatedBy bestCost!!) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                improved = true
                bestCost = specimen.cost
            }
        }
    }
}