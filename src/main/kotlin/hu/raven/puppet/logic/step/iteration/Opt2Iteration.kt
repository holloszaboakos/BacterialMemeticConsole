package hu.raven.puppet.logic.step.iteration

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.LocalSearchAlgorithmState


class Opt2Iteration<C : PhysicsUnit<C>>(
    val calculateCostOf: CalculateCost<C>,
    val algorithmState: LocalSearchAlgorithmState<C>,
) : AlgorithmIteration<LocalSearchAlgorithmState<C>> {

    var sourceIndex = 0
    var permutation = listOf<Int>()

    override fun invoke(algorithmState: LocalSearchAlgorithmState<C>) = algorithmState.run {
        val best = actualCandidate
        var bestCost = best.costOrException()
        var tempGene: Int
        if (sourceIndex == 0) {
            permutation = (0 until best.permutation.indices.count() - 1).shuffled()
        }

        val firstIndex = permutation[sourceIndex]

        for (secondIndex in (firstIndex + 1 until best.permutation.indices.count()).shuffled()) {
            tempGene = best.permutation[firstIndex]
            best.permutation[firstIndex] = best.permutation[secondIndex]
            best.permutation[secondIndex] = tempGene
            best.cost = calculateCostOf(best)
            if (best.costOrException() < bestCost) {
                println(best.cost)
                bestCost = best.costOrException()
            } else {
                tempGene = best.permutation[firstIndex]
                best.permutation[firstIndex] = best.permutation[secondIndex]
                best.permutation[secondIndex] = tempGene
                best.cost = bestCost
            }
        }

        sourceIndex = (sourceIndex + 1) % permutation.size

    }
}