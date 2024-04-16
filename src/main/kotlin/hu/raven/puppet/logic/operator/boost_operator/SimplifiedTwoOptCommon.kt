package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


inline fun swapIfBetter(
    specimen: OnePartRepresentationWithCost,
    position1: Int,
    position2: Int,
    calculateCostOf: CalculateCost<*>,
    onImprovement: () -> Unit = {}
) {
    val bestCost = specimen.costOrException()
    specimen.permutation.swapValues(position1, position2)
    specimen.cost = calculateCostOf(specimen)

    if (specimen.costOrException() dominatesSmaller bestCost) {
        specimen.permutation.swapValues(position1, position2)
        specimen.cost = bestCost
        return
    }

    onImprovement()
}