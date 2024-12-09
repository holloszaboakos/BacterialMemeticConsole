package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


class SimplifiedTwoOptCycleWithRandomOrder<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>
) : BoostOperator<Permutation, S>() {
    override fun invoke(specimen: S) {
        val shuffler = (0..<specimen.representation.size)
            .shuffled()
            .toIntArray()

        for (firstIndexIndex in 0..<specimen.representation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in firstIndexIndex + 1..<specimen.representation.size) {
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