package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
    var stepLimit: Int
) : BoostOperator {
    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()
    var shuffler = intArrayOf()
    val logger : DoubleLogger by KoinJavaComponent.inject(DoubleLogger::class.java)

    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>, specimen: S) {
        logger("BOOST")
        if (lastPositionPerSpecimen.isEmpty()) {
            lastPositionPerSpecimen = Array(algorithm.sizeOfPopulation) { Pair(0, 1) }
        }
        if (shuffler.isEmpty()) {
            shuffler = (0 until algorithm.population.first().permutationSize)
                .shuffled()
                .toIntArray()
        }

        val bestCost = specimen.cost
        var improved = false
        var limitPassed = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]
        var stepCount = 0

        outer@ for (firstIndexIndex in lastPosition.first until algorithm.costGraph.objectives.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            val secondIndexStart =
                if (firstIndexIndex == lastPosition.first) lastPosition.second
                else firstIndexIndex + 1
            for (secondIndexIndex in secondIndexStart until algorithm.population.first().permutationSize) {
                if (stepCount > stepLimit) {
                    lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                    limitPassed = true
                    break@outer
                }
                stepCount++
                val secondIndex = shuffler[secondIndexIndex]
                specimen.swapGenes(firstIndex, secondIndex)
                algorithm.calculateCostOf(specimen)

                if (specimen.cost >= bestCost) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                improved = true
                lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                break@outer
            }
        }

        if (!improved && !limitPassed) {
            lastPosition = Pair(0, 1)
        }
        lastPositionPerSpecimen[specimen.id] = lastPosition
    }
}