package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId


class Opt2StepWithPerSpecimenProgressMemory(
    override val calculateCostOf: CalculateCost
) : BoostOperator<OnePartRepresentationWithCostAndIterationAndId>() {

    private var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId) {
        if (!lastPositionPerSpecimen.containsKey(specimen.id)) {
            lastPositionPerSpecimen[specimen.id] = Pair(0, 1)
        }

        val bestCost = specimen.cost
        var improved = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]!!

        outer@ for (firstIndex in lastPosition.first until specimen.permutation.size - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart until specimen.permutation.size) {
                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

                if (specimen.costOrException() >= bestCost!!) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
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