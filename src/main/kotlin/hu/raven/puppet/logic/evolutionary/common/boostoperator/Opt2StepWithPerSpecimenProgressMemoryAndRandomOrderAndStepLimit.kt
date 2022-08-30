package hu.raven.puppet.logic.evolutionary.common.boostoperator

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>,
    var stepLimit: Int
) : BoostOperator<S> {
    val calculateCostOf: CalculateCost<*> by inject(CalculateCost::class.java)
    val logger: DoubleLogger by inject(DoubleLogger::class.java)

    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()
    var shuffler = intArrayOf()

    override fun invoke(specimen: S) {
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

        outer@ for (firstIndexIndex in lastPosition.first until algorithm.task.costGraph.objectives.size - 1) {
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
                calculateCostOf(specimen)

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