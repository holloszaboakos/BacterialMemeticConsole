package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.FloatArrayExtensions.notDominatedBy

class Opt2CycleWithRandomOrder<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost
) : BoostOperator<O>() {

    private var shuffler = intArrayOf()

    override fun invoke(specimen: O) {
        if (shuffler.isEmpty()) {
            shuffler = (0 ..<specimen.permutation.size)
                .shuffled()
                .toIntArray()
        }


        var bestCost = specimen.cost

        for (firstIndexIndex in 0..<specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in firstIndexIndex + 1..<specimen.permutation.size) {
                val secondIndex = shuffler[secondIndexIndex]

                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

                if (specimen.costOrException() notDominatedBy bestCost!!) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                bestCost = specimen.cost
            }
        }
    }
}