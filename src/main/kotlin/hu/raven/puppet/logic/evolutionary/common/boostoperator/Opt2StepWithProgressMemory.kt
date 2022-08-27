package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class Opt2StepWithProgressMemory : BoostOperator {
    var lastPosition = Pair(0, 1)

    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>, specimen: S) {
        val bestCost = specimen.cost
        var improved = false

        outer@ for (firstIndex in lastPosition.first until algorithm.population.first().permutationSize - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart until algorithm.costGraph.objectives.size) {
                specimen.swapGenes(firstIndex, secondIndex)
                algorithm.calculateCostOf(specimen)

                if (specimen.cost >= bestCost) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                improved = true
                lastPosition = Pair(firstIndex, secondIndex)
                break@outer
            }
        }

        if (!improved) {
            lastPosition = Pair(0, 1)
        }
    }
}