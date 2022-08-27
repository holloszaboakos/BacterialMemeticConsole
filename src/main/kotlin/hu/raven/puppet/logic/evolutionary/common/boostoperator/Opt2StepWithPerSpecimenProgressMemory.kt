package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class Opt2StepWithPerSpecimenProgressMemory : BoostOperator {
    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()

    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>, specimen: S) {
        if (lastPositionPerSpecimen.isEmpty()) {
            lastPositionPerSpecimen = Array(algorithm.sizeOfPopulation) { Pair(0, 1) }
        }

        val bestCost = specimen.cost
        var improved = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]

        outer@ for (firstIndex in lastPosition.first until algorithm.population.first().permutationSize - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart until algorithm.population.first().permutationSize) {
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
        lastPositionPerSpecimen[specimen.id] = lastPosition
    }
}