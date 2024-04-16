package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost

class SimplifiedTwoOptCycleWithRandomOrder<O : OnePartRepresentationWithCost>(
    override val calculateCostOf: CalculateCost<*>
) : BoostOperator<O>() {
    override fun invoke(specimen: O) {
        val shuffler = (0..<specimen.permutation.size)
            .shuffled()
            .toIntArray()

        for (firstIndexIndex in 0..<specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in firstIndexIndex + 1..<specimen.permutation.size) {
                val secondIndex = shuffler[secondIndexIndex]

                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                )
            }
        }
    }
}