package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


inline fun <S : AlgorithmSolution<Permutation, S>> swapIfBetter(
    specimen: S,
    position1: Int,
    position2: Int,
    calculateCostOf: CalculateCost<Permutation, *>,
    onImprovement: () -> Unit = {}
) {
    val bestCost = specimen.costOrException()
    specimen.representation.swapValues(position1, position2)
    specimen.cost = calculateCostOf(specimen.representation)

    if (specimen.costOrException() dominatesSmaller bestCost) {
        specimen.representation.swapValues(position1, position2)
        specimen.cost = bestCost
        return
    }

    onImprovement()
}