package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.diversityofpopulation.DiversityOfPopulation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder(
    override val calculateCostOf: CalculateCost,
    permutationSize: Int,
    populationSize: Int,
) : BoostOperator<OnePartRepresentationWithCostAndIterationAndId>() {

    private var lastPositionPerSpecimen = Array(populationSize) { Pair(0, 1) }
    private var shuffler = (0..<permutationSize)
        .shuffled()
        .toIntArray()

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId) {
        val lastPosition = lastPositionPerSpecimen[specimen.id]

        outer@ for (firstIndexIndex in lastPosition.first..<specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in lastPosition.second..<specimen.permutation.size) {
                val secondIndex = shuffler[secondIndexIndex]

                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    lastPositionPerSpecimen[specimen.id] = Pair(firstIndexIndex, secondIndexIndex)
                    return@invoke
                }
            }
        }


        shuffler.shuffle()
        lastPositionPerSpecimen[specimen.id] = Pair(0, 1)

    }
}