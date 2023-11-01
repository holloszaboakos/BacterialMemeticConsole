package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.FloatArrayExtensions.notDominatedBy


class Opt2StepWithProgressMemory<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost
) : BoostOperator<O>() {

    private var lastPosition = Pair(0, 1)

    override fun invoke(specimen: O) {
        val bestCost = specimen.cost
        var improved = false

        outer@ for (firstIndex in lastPosition.first ..<specimen.permutation.size - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart ..<specimen.objectiveCount) {
                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

                if (specimen.costOrException() notDominatedBy bestCost!!) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                improved = true
                lastPosition = Pair(firstIndex, secondIndex)
                break@outer
            }
        }

        if (!improved) {
            lastPosition = Pair(0, 1)
        }
    }
}