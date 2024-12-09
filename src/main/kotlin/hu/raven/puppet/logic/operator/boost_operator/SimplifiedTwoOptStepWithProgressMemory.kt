package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution


class SimplifiedTwoOptStepWithProgressMemory<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>
) : BoostOperator<Permutation, S>() {

    private var lastPosition = Pair(0, 1)

    override fun invoke(specimen: S) {
        outer@ for (firstIndex in lastPosition.first..<specimen.representation.size - 1) {
            for (secondIndex in lastPosition.second..<specimen.representation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    lastPosition = Pair(firstIndex, secondIndex)
                    return@invoke
                }
            }
        }

        lastPosition = Pair(0, 1)
    }
}