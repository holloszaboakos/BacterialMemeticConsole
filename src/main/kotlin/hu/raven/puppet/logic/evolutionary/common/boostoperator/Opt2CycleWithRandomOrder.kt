package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class Opt2CycleWithRandomOrder : BoostOperator {
    var shuffler = intArrayOf()

    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>, specimen: S) {
        if (shuffler.isEmpty()) {
            shuffler = (0 until algorithm.population.first().permutationSize)
                .shuffled()
                .toIntArray()
        }


        var bestCost = specimen.cost

        for (firstIndexIndex in 0 until algorithm.population.first().permutationSize - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in firstIndexIndex + 1 until algorithm.population.first().permutationSize) {
                val secondIndex = shuffler[secondIndexIndex]

                specimen.swapGenes(firstIndex, secondIndex)
                algorithm.calculateCostOf(specimen)

                if (specimen.cost >= bestCost) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                bestCost = specimen.cost
            }
        }
    }
}