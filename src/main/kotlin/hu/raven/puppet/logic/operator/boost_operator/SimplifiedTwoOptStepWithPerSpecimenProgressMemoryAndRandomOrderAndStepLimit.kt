package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.IndexedOnePartRepresentationWithCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration

class SimplifiedTwoOptStepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
    override val calculateCostOf: CalculateCost<*>,
    private val stepLimit: Int,
    populationSize: Int,
    permutationSize: Int,
) : BoostOperator<IndexedOnePartRepresentationWithCost>() {
    private var lastPositionPerSpecimen = Array(populationSize) { Pair(0, 1) }
    private var shuffler = (0..<permutationSize)
        .shuffled()
        .toIntArray()

    override fun invoke(specimen: IndexedOnePartRepresentationWithCost) {
        val lastPosition = lastPositionPerSpecimen[specimen.index]
        var stepCount = 0

        outer@ for (firstIndexIndex in lastPosition.first..<specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]

            for (secondIndexIndex in lastPosition.second..<specimen.permutation.size) {
                val secondIndex = shuffler[secondIndexIndex]

                if (stepCount == stepLimit) {
                    lastPositionPerSpecimen[specimen.index] = Pair(firstIndexIndex, secondIndexIndex)
                    return
                }

                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    lastPositionPerSpecimen[specimen.index] = Pair(firstIndexIndex, secondIndexIndex)
                    return@invoke
                }

                stepCount++
            }
        }


        lastPositionPerSpecimen[specimen.index] = Pair(0, 1)
        shuffler.shuffle()
    }
}