package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class Opt2 : BoostOperator {

    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>, specimen: S) {
        var improved = true
        var bestCost = specimen.cost

        while (improved) {
            improved = false
            for (firstIndex in 0 until algorithm.population.first().permutationSize - 1) {
                for (secondIndex in firstIndex + 1 until algorithm.population.first().permutationSize) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    algorithm.calculateCostOf(specimen)

                    if (specimen.cost >= bestCost) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    improved = true
                    bestCost = specimen.cost
                }
            }
        }
    }
}