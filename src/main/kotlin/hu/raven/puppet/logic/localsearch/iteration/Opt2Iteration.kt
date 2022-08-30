package hu.raven.puppet.logic.localsearch.iteration

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.localsearch.SLocalSearch
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class Opt2Iteration<S : ISpecimenRepresentation>(
    override val algorithm: SLocalSearch<S>
) : LocalSearchIteration<S> {

    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)

    var sourceIndex = 0
    var permutation = listOf<Int>()

    override fun invoke() = algorithm.run {
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
            calculateCostOf(best)
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
