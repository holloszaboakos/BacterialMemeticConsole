package hu.raven.puppet.logic.localsearch.iteration

import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class Opt2Iteration : LocalSearchIteration {
    var sourceIndex = 0
    var permutation = listOf<Int>()
    override fun <S : ISpecimenRepresentation> invoke(algorithm: SLocalSearch<S>) = algorithm.run {
        val best = actualInstance
        var bestCost = best.cost
        var tempGene: Int
        if (sourceIndex == 0) {
            permutation = (0 until best.permutationIndices.count() - 1).shuffled()
        }

        val firstIndex = permutation[sourceIndex]

        for (secondIndex in (firstIndex + 1 until best.permutationIndices.count()).shuffled()) {
            tempGene = best[firstIndex]
            best[firstIndex] = best[secondIndex]
            best[secondIndex] = tempGene
            algorithm.calculateCostOf(best)
            if (best.cost < bestCost) {
                println(best.cost)
                bestCost = best.cost
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
