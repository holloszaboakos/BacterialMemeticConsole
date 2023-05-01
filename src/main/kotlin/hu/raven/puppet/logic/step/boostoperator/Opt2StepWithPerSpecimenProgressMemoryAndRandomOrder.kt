package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder(
    override val calculateCostOf: CalculateCost
) : BoostOperator<OnePartRepresentationWithCostAndIterationAndId>() {

    private var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()
    private var shuffler = intArrayOf()

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId) {
        if (!lastPositionPerSpecimen.containsKey(specimen.id)) {
            lastPositionPerSpecimen[specimen.id] = Pair(0, 1)
        }
        if (shuffler.isEmpty()) {
            shuffler = (0 until specimen.permutation.size)
                .shuffled()
                .toIntArray()
        }

        val bestCost = specimen.cost
        var improved = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]!!

        outer@ for (firstIndexIndex in lastPosition.first until specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            val secondIndexStart =
                if (firstIndexIndex == lastPosition.first) lastPosition.second
                else firstIndexIndex + 1
            for (secondIndexIndex in secondIndexStart until specimen.permutation.size) {
                val secondIndex = shuffler[secondIndexIndex]
                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

                if (specimen.costOrException() >= bestCost!!) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    specimen.cost = bestCost
                    continue
                }

                improved = true
                lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                break@outer
            }
        }

        if (!improved) {
            lastPosition = Pair(0, 1)
        }
        lastPositionPerSpecimen[specimen.id] = lastPosition
    }
}