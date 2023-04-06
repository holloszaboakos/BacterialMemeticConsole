package hu.raven.puppet.logic.step.localsearchiteration

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.LocalSearchAlgorithmState


class Opt2Iteration<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>,
    val algorithmState: LocalSearchAlgorithmState<C>
) : LocalSearchIteration<C>() {

    var sourceIndex = 0
    var permutation = listOf<Int>()

    override fun invoke() = algorithmState.run {
        val best = actualCandidate
        var bestCost = best.costOrException()
        var tempGene: Int
        if (sourceIndex == 0) {
            permutation = (0 until best.permutationIndices.count() - 1).shuffled()
        }

        val firstIndex = permutation[sourceIndex]

        for (secondIndex in (firstIndex + 1 until best.permutationIndices.count()).shuffled()) {
            tempGene = best[firstIndex]
            best[firstIndex] = best[secondIndex]
            best[secondIndex] = tempGene
            calculateCostOf(best)
            if (best.costOrException() < bestCost) {
                println(best.cost)
                bestCost = best.costOrException()
            } else {
                tempGene = best[firstIndex]
                best[firstIndex] = best[secondIndex]
                best[secondIndex] = tempGene
                best.cost = bestCost
            }
        }

        sourceIndex = (sourceIndex + 1) % permutation.size

    }
}
