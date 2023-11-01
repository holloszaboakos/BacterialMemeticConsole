package hu.raven.puppet.logic.operator.boostoperator

import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.utility.extention.FloatArrayExtensions.notDominatedBy

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit(
    override val calculateCostOf: CalculateCost,
    private val stepLimit: Int,
    populationSize: Int,
    permutationSize: Int,
) : BoostOperator<OnePartRepresentationWithCostAndIterationAndId>() {
    private var lastPositionPerSpecimen = Array(populationSize) { Pair(0, 1) }
    private var shuffler = (0 ..<permutationSize)
        .shuffled()
        .toIntArray()

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId) {
        val bestCost = specimen.cost
        var improved = false
        var limitPassed = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]
        var stepCount = 0

        outer@ for (firstIndexIndex in lastPosition.first ..<specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            val secondIndexStart =
                if (firstIndexIndex == lastPosition.first) lastPosition.second
                else firstIndexIndex + 1
            for (secondIndexIndex in secondIndexStart ..<specimen.permutation.size) {
                if (stepCount > stepLimit) {
                    lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                    limitPassed = true
                    break@outer
                }
                stepCount++
                val secondIndex = shuffler[secondIndexIndex]
                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

                if (specimen.costOrException() notDominatedBy bestCost!!) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
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