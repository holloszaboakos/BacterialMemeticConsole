package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId


class Opt2StepWithPerSpecimenProgressMemory(
    override val calculateCostOf: CalculateCost,
    populationSize: Int
) : BoostOperator<OnePartRepresentationWithCostAndIterationAndId>() {

    private var lastPositionPerSpecimen = Array(populationSize) { Pair(0, 1) }

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId) {
        val lastPosition = lastPositionPerSpecimen[specimen.id]

        outer@ for (firstIndex in lastPosition.first..<specimen.permutation.size - 1) {
            for (secondIndex in lastPosition.second..<specimen.permutation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    lastPositionPerSpecimen[specimen.id] = Pair(firstIndex, secondIndex)
                    return@invoke
                }
            }
        }

        lastPositionPerSpecimen[specimen.id] = Pair(0, 1)
    }
}