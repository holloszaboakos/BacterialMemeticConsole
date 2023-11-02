package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.FloatArrayExtensions.subordinatedBy

inline fun swapIfBetter(
    specimen: OnePartRepresentationWithCost,
    position1: Int,
    position2: Int,
    calculateCostOf: CalculateCost,
    onImprovement: () -> Unit = {}
) {
    val bestCost = specimen.costOrException()
    specimen.permutation.swapValues(position1, position2)
    specimen.cost = calculateCostOf(specimen)

    if (specimen.costOrException() subordinatedBy bestCost) {
        specimen.permutation.swapValues(position1, position2)
        specimen.cost = bestCost
        return
    }

    onImprovement()
}