package hu.raven.puppet.logic.iteration

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution
import hu.raven.puppet.model.state.LocalSearchState


class Opt2Iteration<A : AlgorithmSolution<Permutation, A>>(
    private val calculateCostOf: CalculateCost<Permutation, *>,
) : AlgorithmIteration<LocalSearchState<Permutation, A>> {

    private var sourceIndex = 0
    private var permutation = listOf<Int>()

    override fun invoke(algorithmState: LocalSearchState<Permutation, A>) = algorithmState.run {
        val best = candidateSolution
        var bestCost = best.costOrException()
        var tempGene: Int
        if (sourceIndex == 0) {
            permutation = (0..<best.representation.indices.count() - 1).shuffled()
        }

        val firstIndex = permutation[sourceIndex]

        for (secondIndex in (firstIndex + 1..<best.representation.indices.count()).shuffled()) {
            tempGene = best.representation[firstIndex]
            best.representation[firstIndex] = best.representation[secondIndex]
            best.representation[secondIndex] = tempGene
            best.cost = calculateCostOf(best.representation)

            if (best.costOrException() dominatesSmaller bestCost) {
                tempGene = best.representation[firstIndex]
                best.representation[firstIndex] = best.representation[secondIndex]
                best.representation[secondIndex] = tempGene
                best.cost = bestCost
                continue
            }

            println(best.cost)
            bestCost = best.costOrException()
        }

        sourceIndex = (sourceIndex + 1) % permutation.size

    }
}
