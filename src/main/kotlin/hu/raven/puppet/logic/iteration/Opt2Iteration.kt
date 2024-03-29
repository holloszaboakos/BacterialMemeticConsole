package hu.raven.puppet.logic.iteration

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.state.LocalSearchAlgorithmState


class Opt2Iteration(
    private val calculateCostOf: CalculateCost<*>,
) : AlgorithmIteration<LocalSearchAlgorithmState<*>> {

    private var sourceIndex = 0
    private var permutation = listOf<Int>()

    override fun invoke(algorithmState: LocalSearchAlgorithmState<*>) = algorithmState.run {
        val best = actualCandidate
        var bestCost = best.costOrException()
        var tempGene: Int
        if (sourceIndex == 0) {
            permutation = (0..<best.permutation.indices.count() - 1).shuffled()
        }

        val firstIndex = permutation[sourceIndex]

        for (secondIndex in (firstIndex + 1..<best.permutation.indices.count()).shuffled()) {
            tempGene = best.permutation[firstIndex]
            best.permutation[firstIndex] = best.permutation[secondIndex]
            best.permutation[secondIndex] = tempGene
            best.cost = calculateCostOf(best)

            if (best.costOrException() dominatesSmaller bestCost) {
                tempGene = best.permutation[firstIndex]
                best.permutation[firstIndex] = best.permutation[secondIndex]
                best.permutation[secondIndex] = tempGene
                best.cost = bestCost
                continue
            }

            println(best.cost)
            bestCost = best.costOrException()
        }

        sourceIndex = (sourceIndex + 1) % permutation.size

    }
}
